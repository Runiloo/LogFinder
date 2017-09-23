package forGUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;


class mainGUI extends Container implements Runnable {
    private JTextArea textArea1;
    private JTextField pathTextField;
    private JButton explorerButton;
    private JComboBox comboBox1;
    private JButton searchButton;
    private JPanel panelMain;
    private JScrollPane scrollPane;
    private JTextField searchTextField;
    private final JTree tree;


    private final DefaultMutableTreeNode root;

    private String item;
    private String searchRequest;
    private File searchPath;

    private mainGUI() {
        item = ".log";
        tree = new JTree();
        root = new DefaultMutableTreeNode();
        //EXPLORER'S BUTTON
        explorerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                explorerRun();
            }
        });

        //SEARCH BUTTON
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchRun();
            }
        });
        //CHOSE EXTEND OF FILE
        comboBox1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                item = comboBox1.getSelectedItem().toString();
            }
        });
        //TREE
        tree.addComponentListener(new ComponentAdapter() {
        });
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                try {
                    openFile(tree, textArea1);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int selRow = tree.getRowForLocation(e.getX(), e.getY());
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                    tree.setSelectionPath(selPath);
                    if (selRow > -1) {
                        tree.setSelectionRow(selRow);
                    }
                    JTextArea textArea2 = new JTextArea();
                    try {
                        openFile(tree, textArea2);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    JScrollPane jScrollPaneWindow = new JScrollPane(textArea2);
                    JFrame windowFrame = new JFrame("LogFinder");
                    windowFrame.setContentPane(jScrollPaneWindow);
                    windowFrame.pack();
                    windowFrame.setLocationByPlatform(true);
                    windowFrame.setSize(1280, 960);
                    windowFrame.setVisible(true);
                }
            }
        });

        //
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
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setSize(1280, 960);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws InterruptedException {
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

    private void explorerRun() {
        JFileChooser dialog = new JFileChooser();
        dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dialog.setFileFilter(new MyFileFilter(item));

        int ret = dialog.showDialog(null, "OK");
        if (ret == JFileChooser.APPROVE_OPTION) {
            pathTextField.setText(dialog.getSelectedFile().getAbsolutePath());
            searchPath = dialog.getSelectedFile();
        }
    }

    private void searchRun() {
        if (searchRequest == null || searchRequest.equals(""))
            JOptionPane.showMessageDialog(null, "������� ��������� ������");
        else if (searchPath != null && searchPath.exists()) {
            textArea1.setText(null);
            root.removeAllChildren();
            DefaultTreeModel treeModel = new DefaultTreeModel(root);
            tree.setModel(treeModel);
            scrollPane.setViewportView(tree);

            CreateChildNodes ccn = new CreateChildNodes(searchPath, root, item, treeModel, tree, searchRequest);
            ccn.run();
            tree.setCellRenderer(new MyRenderer(UIManager.getIcon("OptionPane.informationIcon")));
        } else JOptionPane.showMessageDialog(null, "��������� ���������� �� ����������");
    }

    private void openFile(JTree tree, final JTextArea textArea) throws IOException {
        FileReader fileReader = null;
        File file = null;
        BufferedReader[] bufferedReader;
        textArea.setEditable(false);
        if (tree.getSelectionPath() != null) {
            file = new File(tree.getSelectionPath().getLastPathComponent().toString());
        }
        if (file != null && file.isFile()) {
            FileInputStream f = new FileInputStream( file );
            FileChannel ch = f.getChannel( );
            ArrayList<ByteBuffer> byteBuffer = new ArrayList<ByteBuffer>();

            int nRead, nGet, j=0;
            textArea.setText(null);
            ByteBuffer buffer = ByteBuffer.allocate( 409600 );
            while ( (nRead=ch.read( buffer )) != -1 ) {
                if (nRead == 0)
                    continue;
                byteBuffer.add(buffer);
            }
            ByteBuffer byteBuffer1 = ByteBuffer.allocate( 409600 ;
            for(int ff = 0; ff<2;ff++) {
                byteBuffer1 = byteBuffer.get(ff);

                byteBuffer1.position(0);
                byteBuffer1.limit(nRead);
                while (byteBuffer.hasRemaining()) {
                    nGet = Math.min(byteBuffer.remaining(), 128);
                    byteBuffer.get(byteArray, 0, nGet);
                    textArea.append(new String(byteArray, "windows-1251"));
                }
                byteBuffer.clear();
            }
            byte[] byteArray = new byte[128];
            byteBuffer.get(0).get()




            /*while ( (nRead=ch.read( byteBuffer )) != -1 ) {
                if (nRead == 0)
                    continue;
                byteBuffer.position(0);
                byteBuffer.limit(nRead);
                while (byteBuffer.hasRemaining()) {
                    nGet = Math.min(byteBuffer.remaining(), 128);
                    byteBuffer.get(byteArray, 0, nGet);
                    textArea.append(new String(byteArray, "windows-1251"));
                }
                byteBuffer.clear();

            }*/
    }
}







    /*fileReader = new FileReader(file);

            for(int i = 0; i< 100; i++) {
        textArea.append(bufferedReader.readLine());
        textArea.append("\n");
    }
}
    final BufferedReader finalBufferedReader = bufferedReader;
        textArea.addMouseWheelListener(new MouseWheelListener() {
public void mouseWheelMoved(MouseWheelEvent e) {
        int i = e.getWheelRotation();
        if(i==-1&&finalBufferedReader!=null) {
        try {
        for(int j = 0; j< 10; j++) {
        textArea.append(finalBufferedReader.readLine());
        textArea.append("\n");
        }
        } catch (IOException e1) {
        e1.printStackTrace();
        }
        }*/



            /*FileInputStream f = new FileInputStream( file );
            FileChannel ch = f.getChannel( );
            ch.re
            ByteBuffer byteBuffer = ByteBuffer.allocate( 1024 );
            byte[] byteArray = new byte[128];
            int nRead, nGet, j=0;
            textArea.setText(null);
            while ( (nRead=ch.read( byteBuffer )) != -1 )
            {
                if ( nRead == 0 )
                    continue;
                byteBuffer.position( 0 );
                byteBuffer.limit( nRead );
                while( byteBuffer.hasRemaining( ) )
                {
                    nGet = Math.min( byteBuffer.remaining( ), 128 );
                    byteBuffer.get( byteArray, 0, nGet );
                    textArea.append(new String(byteArray, "windows-1251"));
                }
                byteBuffer.clear( );*/

            /*try {
                fileReader = new FileReader(file);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            textArea.setText(null);
            char buffer[] = new char[4096];
            int len;
            try {
                while ((len = fileReader.read(buffer)) != -1) {
                    String s = new String(buffer, 0, len);
                    textArea.append(s);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                fileReader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            textArea.setCaretPosition(0);*/
    }
}

