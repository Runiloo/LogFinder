package forGUI;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by Runilog on 19.09.2017.
 */
public class MyFinder implements Runnable{
    DefaultMutableTreeNode root;
    String searchRequest;
    public void MyFinder(DefaultMutableTreeNode root, String searchRequest){
        this.root = root;
        this.searchRequest = searchRequest;
    }

    public void run(){

    }


}
