package forGUI;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.util.Stack;

/**  ласс дл€ создани€ узлов дерева хран€щих директории и файлы содержащии поисковый запрос*/
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

    /** ћетод, запускающий создание узлов*/
    public void runCreating() {
        createChildren(file, root);
    }

    /** ћетод, создающий узлы дерева
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
    /** ћетод добавл€ющий файлы только файлы с встречающимс€ поисковым запросом
     * @param fileRoot
     * @param node */
    private void pushFiles(File fileRoot, DefaultMutableTreeNode node) {
        File[] files = fileRoot.listFiles();
        if (files == null) return;
        FileScan fs = new FileScan(files, this, node);
        fs.start();
    }

    /** ћетод сканирующий путь и распредел€ющий в зависимости от того директори€ или файл
     * на запись в узлы
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
    /** ћетод безопасно обновл€ющий отображение дерева в интерфейсе, так как сканирование происходит
     * в несколько потоков*/
    private synchronized void treeUpdate() {
        treeModel.reload();
        for (int i = 0; i < tree.getRowCount(); i++) {
            if (!tree.isExpanded(i))
                tree.expandRow(i);
        }
    }

}
