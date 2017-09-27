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
 * Основной класс приложения, для поиска текстовых файлов с заданным текстом.
 */

class LogFinder extends Container implements Runnable {
    /** Поля с обявлением основных элементов интерфейса */
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

    /** переменная для хранения расширения файла */
    private String fileExtension;
    /** переменная для хранения поискового запроса*/
    private String searchRequest;
    /** переменная для хранения адреса директории где будет проводиться поиск*/
    private File searchPath;

    /** Конструктор класса*/
    private LogFinder() {
        fileExtension = ".log";
        tree = new JTree();
        root = new DefaultMutableTreeNode();

        /** присвоение кнопке для вызова диспетчера файлов, компонента для реакции на нажатие*/
        explorerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                explorerRun();
            }
        });
        /** присвоение кнопке для запуска поиска, компонента для реакции на нажатие*/
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchRun();
            }
        });
        /** присвоение выпадающему списку для выбора расширения искомого файла компонента
         * для получения выбранного расширения*/
        extensionChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileExtension = extensionChooser.getSelectedItem().toString();
            }
        });
        /** присвоение дереву файловой системы с найденными файлами компонента для
         * рекции на нажатие кнопок мыши*/
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // правая кнопка мыши
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
                //левая кнопка мыши
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

        /** присвоение полю ввода пути поиска компонента для считывания вводимого текста с клавиатуры*/
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
        /** присвоение полю ввода поискового запроса компонента для считывания вводимого текста с клавиатуры*/
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
    /** Метод для запуска потока в котором будет выполнятся приложение*/
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
    /** Метод для запуска диспетчера файлов*/
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

    /** Метод, выполняющий поиск по файловой системе файлов с текстом поискового запроса*/
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
    /** Метод, очуществляющий открытие содержимого файла в основном окне либо в отдельном окне
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
    /** Метод для определения наличия в файле поискового запроса
     * @param file
     * @param searchRequest
     * @return объект класса IntAndByteBuffer*/
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

