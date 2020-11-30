package FiSy;


import java.nio.ByteBuffer;

public class ByteUtils {
        private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    static byte[] longToByteArray(long value) {
        return ByteBuffer.allocate(8).putLong(value).array();
    }

    static long byteArrayToLong(byte[] array) {
        return ByteBuffer.wrap(array).getLong();
    }
    }

