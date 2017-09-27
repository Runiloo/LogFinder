package forGUI;

import javax.swing.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Класс реализующий интерфейс компонента для реагирования на перемещение слайдера в полосе прокрутки
 * и подгрузки текста из файла в соответствии с положением слайдера
 */
class MyAdjustmentListener implements AdjustmentListener {
    private JTextArea textArea;
    private File file;
    private JScrollBar scrollBar;

    /**
     * @param file
     * @param scrollBar
     * @param textArea */
    public MyAdjustmentListener(File file, JScrollBar scrollBar, JTextArea textArea){
        this.textArea = textArea;
        this.scrollBar = scrollBar;
        this.file = file;
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        textArea.setText(null);

        textArea.getHighlighter().removeAllHighlights();
        ByteBuffer byteBuffers = null;
        try {
            byteBuffers = getTextByIndex(file, e.getValue());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if (byteBuffers!=null) {
            String text = null;
            try {
                text = new String(byteBuffers.array(), "windows-1251").split("\\u0000", 2)[0];
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            textArea.append(text);
            textArea.setCaretPosition(0);
        }

    }

    /** Метод производящий поиск в файле указанного фрагмента по индексу
     * @param file
     * @param index
     * @return ByteBuffer*/
    private ByteBuffer getTextByIndex(File file, int index) throws IOException {
        FileInputStream f = new FileInputStream( file );
        FileChannel ch = f.getChannel( );
        ByteBuffer byteBuffer = ByteBuffer.allocate( 102400 );
        int nRead, count=0;
        while ( (nRead=ch.read( byteBuffer )) != -1) {
            if ( nRead == 0 )
                continue;
            byteBuffer.position( 0 );
            if(count==index)
                return byteBuffer;
            count++;
            byteBuffer = ByteBuffer.allocate( 102400 );
        }
        ch.close();
        f.close();
        return null;
    }
}
