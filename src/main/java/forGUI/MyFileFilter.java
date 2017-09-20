package forGUI;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by Runilog on 14.09.2017.
 */
public class MyFileFilter extends javax.swing.filechooser.FileFilter {

    String ext, description;

    public MyFileFilter(String ext, String description) {
        this.ext = ext;
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    //В этом методе может быть любая проверка файла
    @Override
    public boolean accept(File f) {
        if (f != null) {
            if (f.isDirectory()) {
                return true;
            }
            return f.toString().endsWith(ext);
        }
        return false;
    }


}
