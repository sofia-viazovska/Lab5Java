package task3;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Фільтр-потік читання: символи, прочитані з вкладеного Reader, розшифровуються
 * перед тим, як потрапити до того, хто читає.
 */
public class DecryptingReader extends FilterReader {

    private final CharShiftCipher cipher;

    public DecryptingReader(Reader in, char key) {
        super(in);
        this.cipher = new CharShiftCipher(key);
    }

    @Override
    public int read() throws IOException {
        int c = in.read();
        return c == -1 ? -1 : cipher.decrypt((char) c);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int n = in.read(cbuf, off, len);
        for (int i = 0; i < n; i++) {
            cbuf[off + i] = cipher.decrypt(cbuf[off + i]);
        }
        return n;
    }
}
