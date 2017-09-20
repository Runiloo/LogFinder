package forGUI;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Runilog on 18.09.2017.
 */
public class CreateChildNodes implements Runnable {
    private DefaultMutableTreeNode root;
    private File fileRoot;
    private String item;
    private JTree tree;

    public CreateChildNodes(File fileRoot, DefaultMutableTreeNode root, String item/*,JTree tree*/) {
        this.fileRoot = fileRoot;
        this.root = root;
        this.item = item;
        this.tree = tree;
    }

    public void run() {
        createChildren(fileRoot, root);
    }

    private synchronized void createChildren(File fileRoot, DefaultMutableTreeNode node) {
        Stack<String> pathStack = new Stack<String>();
        String buff;
        File fBuff = fileRoot;

        while (fBuff.getParent() != null) {
            pathStack.push(fBuff.getParent());
            buff = fBuff.getParent();
            fBuff = new File(buff);
        }
        node.setUserObject(pathStack.pop());
        while (!pathStack.isEmpty()) {
            DefaultMutableTreeNode nodeBuff = new DefaultMutableTreeNode(pathStack.pop());
            node.add(nodeBuff);
            node = nodeBuff;

//            tree.updateUI();
        }
        pushFiles(fileRoot, node);
    }

    private synchronized void pushFiles(File fileRoot, DefaultMutableTreeNode node) {
        File[] files = fileRoot.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.toString().endsWith(item)) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new FileNode(file));
                node.add(childNode);
//                tree.updateUI();
            }
            if (file.isDirectory()) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new FileNode(file));
                node.add(childNode);
                pushFiles(file, childNode);
            }
        }
    }
}
