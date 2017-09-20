package forGUI;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.ArrayList;
import java.util.Stack;


public class CreateChildNodes implements Runnable {
    private DefaultMutableTreeNode root;
    private File fileRoot;
    private String item;
    private JTree tree;
    Boolean isFind;

    protected CreateChildNodes(File fileRoot, DefaultMutableTreeNode root, String item,JTree tree, Boolean isFind) {
        this.fileRoot = fileRoot;
        this.root = root;
        this.item = item;
        this.tree = tree;
        this.isFind = isFind;
    }

    public void run() {
        createChildren(fileRoot, root);
    }

    private void createChildren(File fileRoot, DefaultMutableTreeNode node) {
        Stack<String> pathStack = new Stack<String>();
        File fBuff = fileRoot;
        pathStack.push(fBuff.getPath());

        while (fBuff.getParent() != null) {
            pathStack.push(fBuff.getParent());
            fBuff = new File(fBuff.getParent());
        }

        node.setUserObject(pathStack.pop());

        while (!pathStack.isEmpty()) {
            DefaultMutableTreeNode nodeBuff = new DefaultMutableTreeNode(pathStack.pop());
            node.add(nodeBuff);
            node = nodeBuff;
        }
        pushFiles(fileRoot, node);

    }

    private void pushFiles(File fileRoot, DefaultMutableTreeNode node) {
        File[] files = fileRoot.listFiles();
        if (files == null) return;
        FileScan fs = new FileScan(files, this, node, isFind);
        fs.start();

    }

    protected void Scan(File file, DefaultMutableTreeNode node) {
        if (file.toString().endsWith(item)) {
//            if (isFind)
                node.add(new DefaultMutableTreeNode(new FileNode(file)));

                /*SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        tree.updateUI();
                    }
                });*/
        }
        if (file.isDirectory()) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new FileNode(file));
            node.add(childNode);
            pushFiles(file, childNode);
        }
    }
}
