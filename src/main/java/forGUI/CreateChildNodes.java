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
    String searchRequest;

    protected CreateChildNodes(File fileRoot, DefaultMutableTreeNode root, String item,JTree tree, Boolean isFind, String searchRequest) {
        this.fileRoot = fileRoot;
        this.root = root;
        this.item = item;
        this.tree = tree;
        this.isFind = isFind;
        this.searchRequest = searchRequest;
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
        FileScan fs = new FileScan(files, this, node);
        fs.start();
    }

    protected void Scan(File file, DefaultMutableTreeNode node) {
        if (file.toString().endsWith(item)) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(new File(file.getPath()));
                child.setAllowsChildren(false);
                node.add(child);
        }
        if (file.isDirectory()) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new File(file.getPath()));
            node.add(childNode);
            pushFiles(file, childNode);
        }
    }
    public String getSearchRequest(){
        return searchRequest;
    }
}
