package forGUI;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/** Change tree elements icons by type*/
class MyRenderer extends DefaultTreeCellRenderer {

    private final Icon icon;

    /**
     * @param icon*/
    MyRenderer(Icon icon){
    this.icon = icon;
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,int row, boolean hasFocus){
        super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus);
        DefaultMutableTreeNode val = (DefaultMutableTreeNode)value;
        if(leaf&&!val.getAllowsChildren()){
            setIcon(icon);
        }
        else if(leaf&&val.getAllowsChildren())
            setIcon(UIManager.getIcon("FileView.directoryIcon"));
        return this;
    }


}
