package forGUI;

import java.nio.ByteBuffer;

/**
 * Container for two values different types
 */
class IntAndByteBuffer {
    private final int[] index;
    private final ByteBuffer byteBuffer;

    /**
     * @param index
     * @param byteBuffer */
    public IntAndByteBuffer(int[] index, ByteBuffer byteBuffer){
        this.index = index;
        this.byteBuffer = byteBuffer;
    }

    /**
     * @return int[]*/
    public int[] getIndex(){
        return index;
    }
    /** @return ByteBuffer*/
    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }
}
