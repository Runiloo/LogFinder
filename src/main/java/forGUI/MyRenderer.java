package forGUI;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;

/**
 * Created by Runilog on 19.09.2017.
 */
public class MyRenderer extends DefaultTreeCellRenderer {

    Icon icon;
    private boolean leaf;

    public MyRenderer(Icon icon){
    this.icon = icon;
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,int row, boolean hasFocus){
        super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus);
        if(leaf){
            setIcon(icon);
        }

        return this;
    }


}
