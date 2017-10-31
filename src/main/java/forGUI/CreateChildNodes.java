package forGUI;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.util.Stack;

/** Create tree nodes with path to files that coincide with request*/
class CreateChildNodes extends Thread  {
    private final DefaultMutableTreeNode root;
    private final File file;
    private final String fileExtension;
    private final DefaultTreeModel treeModel;
    private final JTree tree;
    private final String searchRequest;

    /**
     * @param file
     * @param root
     * @param fileExtension
     * @param treeModel
     * @param tree
     * @param searchRequest
     * */
    CreateChildNodes(File file, DefaultMutableTreeNode root, String fileExtension, DefaultTreeModel treeModel, JTree tree, String searchRequest) {
        this.file = file;
        this.root = root;
        this.fileExtension = fileExtension;
        this.treeModel = treeModel;
        this.searchRequest = searchRequest;
        this.tree = tree;
    }

    /** Run nodes creating*/
    public void runCreating() {
        createChildren(file, root);
    }

    /** Create tree nodes
     * @param fileRoot
     * @param node */
    private void createChildren(File fileRoot, DefaultMutableTreeNode node) {
        Stack<String> pathStack = new Stack<String>();
        File fileBuffer = fileRoot;
        pathStack.push(fileBuffer.getPath());

        while (fileBuffer.getParent() != null) {
            pathStack.push(fileBuffer.getParent());
            fileBuffer = new File(fileBuffer.getParent());
        }
        node.setUserObject(pathStack.pop());
        while (!pathStack.isEmpty()) {
            DefaultMutableTreeNode nodeBuff = new DefaultMutableTreeNode(pathStack.pop());
            node.add(nodeBuff);
            node = nodeBuff;
        }
        pushFiles(fileRoot, node);
    }
    /** Add only files with request text
     * @param fileRoot
     * @param node */
    private void pushFiles(File fileRoot, DefaultMutableTreeNode node) {
        File[] files = fileRoot.listFiles();
        if (files == null) return;
        FileScan fs = new FileScan(files, this, node);
        fs.start();
    }

    /** Scan path and distribute the files and folders
     * @param file
     * @param node */
    void Scan(File file, DefaultMutableTreeNode node) {
        if (file.toString().endsWith(fileExtension)) {
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
    /** Refresh tree safely in UI, because of multithreaded scan*/
    private synchronized void treeUpdate() {
        treeModel.reload();
        for (int i = 0; i < tree.getRowCount(); i++) {
            if (!tree.isExpanded(i))
                tree.expandRow(i);
        }
    }

}
