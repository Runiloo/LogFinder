package forGUI;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Scan files and search matches with search request
 */
class FileScan extends Thread {
    private final File[] files;
    private final CreateChildNodes ccn;
    private final DefaultMutableTreeNode node;
    private final String searchRequest;

    /**
     * @param files
     * @param ccn
     * @param node */
    public FileScan(File[] files, CreateChildNodes ccn, DefaultMutableTreeNode node) {
        this.files = files;
        this.ccn = ccn;
        this.node = node;
        searchRequest = ccn.getSearchRequest();
    }
    /** Run scan in new thread*/
    public void run() {
        for (File file : files) {
            if(file.isDirectory()) ccn.Scan(file, node);
            else try {
                if(parseFile(file,searchRequest)) ccn.Scan(file, node);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /** Scan file to find request text
     * @param file
     * @param searchRequest */
    private boolean parseFile(File file, String searchRequest) throws IOException {
        byte[] searchRequestBytes = searchRequest.getBytes();
        FileInputStream f = new FileInputStream( file );
        FileChannel ch = f.getChannel( );
        ByteBuffer byteBuffer = ByteBuffer.allocate( 1024 );
        byte[] byteArray = new byte[128];
        int nRead, nGet, j=0;
        while ( (nRead=ch.read( byteBuffer )) != -1 )
        {
            if ( nRead == 0 )
                continue;
            byteBuffer.position( 0 );
            byteBuffer.limit( nRead );
            while( byteBuffer.hasRemaining( ) )
            {
                nGet = Math.min( byteBuffer.remaining( ), 128 );
                byteBuffer.get( byteArray, 0, nGet );
                for ( int i=0; i<nGet; i++ ){
                    for(; j < searchRequest.length(); j++){
                        byte bu = byteArray[i];
                        if(bu!=searchRequestBytes[j]) {
                            j=0;
                            break;
                        }
                        i++;
                        if(j == searchRequestBytes.length-1)
                            return true;
                    }
                }
            }
            byteBuffer.clear( );
        }
        ch.close();
        f.close();
        return false;
    }
}

