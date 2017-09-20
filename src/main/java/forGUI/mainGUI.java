package forGUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.SwingUtilities;

/**
 * Created by Runilog on 13.09.2017.
 */
public class mainGUI extends Container implements Runnable {
    private JTextArea textArea1;
    private JTextField pathTextField;
    private JButton explorerButton;
    private JComboBox comboBox1;
    private JButton searchButton;
    private JPanel panelMain;
    private JScrollPane scrollPane;
    private JTextField searchTextField;
    private String item = ".log";
    private DefaultMutableTreeNode root;
    private DefaultTreeModel treeModel;
    private JTree tree;
    private String searchRequest;
    private String treePath;
    private File searchPath;
    private Boolean isFind = true;



    public mainGUI() {
        tree = new JTree();
        root = new DefaultMutableTreeNode();
        explorerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JFileChooser dialog = new JFileChooser();
                dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                dialog.setFileFilter(new MyFileFilter(item, ""));

                int ret = dialog.showDialog(null, "Выбрать");
                  if(ret == JFileChooser.APPROVE_OPTION) {
                      pathTextField.setText(dialog.getSelectedFile().getAbsolutePath());
                      searchPath =  dialog.getSelectedFile();
                  }
            }
        });
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(searchRequest==null||searchRequest.equals("")) JOptionPane.showMessageDialog(null,"Введите поисковый запрос");
                if (searchPath!=null&&searchPath.exists()) {
                    root.removeAllChildren();

                    DefaultMutableTreeNode child = new DefaultMutableTreeNode(searchPath);
                    treeModel = new DefaultTreeModel(root);
                    tree.setModel(treeModel);
                    scrollPane.setViewportView(tree);

                    CreateChildNodes ccn = new CreateChildNodes(searchPath, root, item/*, tree*/);
                    SwingUtilities.invokeLater(ccn);
//                    ccn.run();
                    for (int i =
                         0; i < tree.getRowCount(); i++)
                        tree.expandRow(i);

                    if(!isFind)
                    tree.setCellRenderer(new MyRenderer(UIManager.getIcon("FileView.directoryIcon")));
                    else  tree.setCellRenderer(new MyRenderer(UIManager.getIcon("OptionPane.informationIcon")));
                }
                else JOptionPane.showMessageDialog(null,"Указанная директория не существует");
            }
        });


        comboBox1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            item = comboBox1.getSelectedItem().toString();
            }
        });
        //tree
        tree.addComponentListener(new ComponentAdapter() {
        });
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                //tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            }
        });

        pathTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                searchPath = new File(pathTextField.getText());
            }

            public void removeUpdate(DocumentEvent e) {
                searchPath = new File(pathTextField.getText());
            }

            public void changedUpdate(DocumentEvent e) {
                searchPath = new File(pathTextField.getText());
            }
        });

        searchTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                searchRequest = searchTextField.getText();
            }

            public void removeUpdate(DocumentEvent e) {
                searchRequest = searchTextField.getText();
            }

            public void changedUpdate(DocumentEvent e) {
                searchRequest = searchTextField.getText();
            }
        });
        /*scrollPane.addMouseListener(new MouseAdapter() {
        });*/
    }

    public void run() {
        JFrame frame = new JFrame("LogFinder");
        frame.setContentPane(new mainGUI().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setSize(1280, 960);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new mainGUI());

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

}
