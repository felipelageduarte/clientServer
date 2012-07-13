package Server;

import Log.Log;
import NIOServerFramework.BufferFactory;
import java.nio.ByteBuffer;

public class Buffer implements BufferFactory {

    private int capacity;

    public Buffer(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public ByteBuffer newBuffer() {
        return (ByteBuffer.allocate(capacity));
    }

    @Override
    public void returnBuffer(ByteBuffer buffer) {
        Log.warn("Not supported yet.");
    }
}
