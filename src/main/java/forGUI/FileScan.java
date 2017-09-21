package forGUI;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;

/**
 * Created by Runilog on 21.09.2017.
 */
public class FileScan extends Thread {
    private File[] files;
//    private String findIt;
    private Boolean isFind;
    private CreateChildNodes ccn;
    private DefaultMutableTreeNode node;
    private String searchRequest;

    public FileScan(File[] files, CreateChildNodes ccn, DefaultMutableTreeNode node) {
        this.files = files;
        //findIt = searchRequest;
//        this.isFind = isFind;
        this.ccn = ccn;
        this.node = node;
        searchRequest = ccn.getSearchRequest();
    }

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

    public boolean parseFile(File file, String searchRequest) throws IOException {
        RandomAccessFile ra = new RandomAccessFile(file, "r");
        int i;

        while(ra.getFilePointer()<ra.length()){
           i = 0;
            for(; i < searchRequest.length(); i++){
                byte[] bu = {ra.readByte()};
                String a = new String(bu);
                if(!a.equals(searchRequest.substring(i,i+1))) {
                    break;
                }
                if (i == searchRequest.length()-1)
                return true;
            }

        }
        if (ra != null)  ra.close();
        return false;
    }
}

