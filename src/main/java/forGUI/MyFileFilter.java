package forGUI;

import java.io.File;

/** Create filter for file extstension */
class MyFileFilter extends javax.swing.filechooser.FileFilter {
    private final String ext;
    private final String description;

    /**
     * @param ext */
    public MyFileFilter(String ext) {
        this.ext = ext;
        this.description = "";
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean accept(File f) {
        if (f != null) {
            if (f.isDirectory()) {
                return true;
            }
            try {
                return f.toString().endsWith(ext);
            }
            catch (NullPointerException ignored){
            }
        }
        return false;
    }
}
