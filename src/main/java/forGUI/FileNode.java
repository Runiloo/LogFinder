package forGUI;

import java.io.File;

/**
 * Created by Runilog on 18.09.2017.
 */
public class FileNode {
    private File file;

    public FileNode (File file){
        this.file = file;
    }

    @Override
    public String toString(){
        String name = file.getName();
        if(name.equals("")) return file.getAbsolutePath();
        else return  name;
    }
}
