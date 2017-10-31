package forGUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Main class of programm
 */

class LogFinder extends Container implements Runnable {
    /** Fields with UI elements  */
    private JTextField pathTextField;
    private JButton explorerButton;
    private JComboBox extensionChooser;
    private JButton searchButton;
    private JPanel panelMain;
    private JScrollPane scrollPane;
    private JTextField searchTextField;
    private JScrollPane scrollPaneTextArea;
    private JScrollBar scrollBar;
    private JLabel filePathText;
    private final JTree tree;
    private final DefaultMutableTreeNode root;

    /** Keep extension of file */
    private String fileExtension;
    /** Keep search request*/
    private String searchRequest;
    /** Keep folder path for scan*/
    private File searchPath;
    
    private LogFinder() {
        fileExtension = ".log";
        tree = new JTree();
        root = new DefaultMutableTreeNode();

        /** Add listener to explorer button*/
        explorerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                explorerRun();
            }
        });
        /** Add listener to search run button*/
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchRun();
            }
        });
        /** Add listener to drop list(choosing file extension)*/
        extensionChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileExtension = extensionChooser.getSelectedItem().toString();
            }
        });
        /** Add listener to tree*/
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Right mouse button
                if (SwingUtilities.isRightMouseButton(e)) {
                    int selRow = tree.getRowForLocation(e.getX(), e.getY());
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                    tree.setSelectionPath(selPath);
                    if (selRow > -1) {
                        tree.setSelectionRow(selRow);
                    }
                    JTextArea textArea2 = new JTextArea();
                    JScrollPane jScrollPaneWindow = new JScrollPane(textArea2);
                    jScrollPaneWindow.getVerticalScrollBar().setVisible(false);
                    JScrollBar scrollBar = new JScrollBar(Adjustable.VERTICAL);

                    JFrame windowFrame = new JFrame(tree.getSelectionPath().getLastPathComponent().toString());
                    windowFrame.getContentPane().setLayout(new BoxLayout( windowFrame.getContentPane(),BoxLayout.X_AXIS));
                    windowFrame.getContentPane().add(scrollBar);
                    windowFrame.getContentPane().add(jScrollPaneWindow);
                    windowFrame.pack();
                    windowFrame.setLocationByPlatform(true);
                    windowFrame.setSize(1280, 960);
                    windowFrame.setVisible(true);
                    try {
                        openFile(tree, textArea2, scrollBar);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
                //Left mouse button
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int selRow = tree.getRowForLocation(e.getX(), e.getY());
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                    tree.setSelectionPath(selPath);
                    filePathText.setText(tree.getSelectionPath().getLastPathComponent().toString());
                    if (selRow > -1) {
                        tree.setSelectionRow(selRow);
                    }
                    JTextArea textArea = new JTextArea();
                    scrollPaneTextArea.setViewportView(textArea);
                    scrollPaneTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                    try {
                        openFile(tree, textArea , scrollBar);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        });

        /** Add listener to field to react on putting text */
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
        /** Add listener to field to read putted text*/
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
    }
    /** Run main thread for program*/
    public void run() {
        JFrame frame = new JFrame("LogFinder");
        frame.setContentPane(new LogFinder().panelMain);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setSize(1280, 960);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws InterruptedException {
        SwingUtilities.invokeLater(new LogFinder());
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
    /** Run explorer*/
    private void explorerRun() {
        JFileChooser dialog = new JFileChooser();
        dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dialog.setFileFilter(new MyFileFilter(fileExtension));

        int ret = dialog.showDialog(null, "OK");
        if (ret == JFileChooser.APPROVE_OPTION) {
            pathTextField.setText(dialog.getSelectedFile().getAbsolutePath());
            searchPath = dialog.getSelectedFile();
        }
    }

    /** Run search*/
    private void searchRun() {
        if (searchRequest == null || searchRequest.equals(""))
            JOptionPane.showMessageDialog(null, "Введите поисковый запрос");
        else if (searchPath != null && searchPath.exists()) {
            tree.setCellRenderer(new MyRenderer(UIManager.getIcon("OptionPane.informationIcon")));
            root.removeAllChildren();
            DefaultTreeModel treeModel = new DefaultTreeModel(root);
            tree.setModel(treeModel);
            scrollPane.setViewportView(tree);
            CreateChildNodes ccn = new CreateChildNodes(searchPath, root, fileExtension, treeModel, tree, searchRequest);
            ccn.runCreating();
        } else JOptionPane.showMessageDialog(null, "Указанная директория не существует");
    }
    /** Open file in program window or new window
     * @param tree
     * @param textArea
     * @param scrollBar
     * */
    private void openFile(JTree tree, JTextArea textArea, final JScrollBar scrollBar) throws IOException {
        File file = null;
        if (tree.getSelectionPath() != null) {
            file = new File(tree.getSelectionPath().getLastPathComponent().toString());
        }
        if (file != null && file.isFile()) {
            //textArea.setText(null);
            IntAndByteBuffer find = parseFile(file, searchRequest);
            int[] partNumWithFoundText = find.getIndex();
            ByteBuffer byteBuffers = find.getByteBuffer();

            int caretPos = 0;
            if(byteBuffers!=null) {
                String text = new String(byteBuffers.array(), "windows-1251").split("\\u0000", 2)[0];
                textArea.append(text);
                int foundStart=(text.indexOf(searchRequest));
                if(foundStart >= 0 ) {
                    caretPos = foundStart;
                    int foundEnd = (foundStart + searchRequest.length());
                    try {
                        textArea.getHighlighter().addHighlight((foundStart), foundEnd, new DefaultHighlighter.DefaultHighlightPainter(Color.red));
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }
            textArea.setCaretPosition(caretPos);

            try {
                int line = textArea.getLineOfOffset(textArea.getCaretPosition()+1);
                int start = textArea.getLineStartOffset(line);
                textArea.setCaretPosition(start);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            scrollBar.setVisibleAmount(1);
            scrollBar.setMaximum(partNumWithFoundText[1]);
            scrollBar.setValue(partNumWithFoundText[0]);
            scrollBar.setUnitIncrement(1);
            scrollBar.addAdjustmentListener(new MyAdjustmentListener(file,scrollBar,textArea));
        }
    }
    /** Parse file to find out search request text
     * @param file
     * @param searchRequest
     * @return instance of IntAndByteBuffer class with results*/
    private IntAndByteBuffer parseFile(File file, String searchRequest) throws IOException {
        int partsCount[] ={0,0};
        ByteBuffer result = ByteBuffer.allocate( 102400 );
        byte[] searchRequestBytes = searchRequest.getBytes();
        FileInputStream f = new FileInputStream( file );
        FileChannel ch = f.getChannel( );
        ByteBuffer byteBuffer = ByteBuffer.allocate( 102400 );
        byte[] byteArray = new byte[128];
        int nRead, nGet, j=0;
        boolean isFound = false;
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
                for ( int i=0; i<nGet; i++ ){
                    for(; j < searchRequest.length(); j++){
                        byte bu = byteArray[i];
                        if(!(bu==searchRequestBytes[j])) {
                            j=0;
                            break;
                        }
                        i++;
                        if(j == searchRequestBytes.length-1) {
                            isFound = true;
                            byteBuffer.position(0);
                            result = byteBuffer;
                        }
                    }
                }
            }
            if(!isFound) partsCount[0]++;
            partsCount[1]++;
            byteBuffer = ByteBuffer.allocate( 102400 );
            byteBuffer.clear( );
        }
        ch.close();
        f.close();
        return new IntAndByteBuffer(partsCount, result);
    }

}

