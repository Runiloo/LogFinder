package forGUI;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.util.Stack;


class CreateChildNodes extends Thread implements Runnable {
    private final DefaultMutableTreeNode root;
    private final File fileRoot;
    private final String item;
    private final DefaultTreeModel treeModel;
    private final JTree tree;
    private final String searchRequest;

    CreateChildNodes(File fileRoot, DefaultMutableTreeNode root, String item, DefaultTreeModel treeModel, JTree tree, String searchRequest) {
        this.fileRoot = fileRoot;
        this.root = root;
        this.item = item;
        this.treeModel = treeModel;
        this.searchRequest = searchRequest;
        this.tree = tree;
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

    void Scan(File file, DefaultMutableTreeNode node) {
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
        treeUpdate();
    }

    public String getSearchRequest() {
        return searchRequest;
    }

    private synchronized void treeUpdate() {
        treeModel.reload();
        for (int i = 0; i < tree.getRowCount(); i++) {
            if (!tree.isExpanded(i))
                tree.expandRow(i);
        }
    }

}
