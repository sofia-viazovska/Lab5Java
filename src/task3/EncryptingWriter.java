package task3;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Фільтр-потік запису: усе, що в нього пишуть, шифрується й передається далі
 * у вкладений Writer. Перевизначено всі три методи write, бо FilterWriter
 * просто делегує їх вкладеному потоку без змін.
 */
public class EncryptingWriter extends FilterWriter {

    private final CharShiftCipher cipher;

    public EncryptingWriter(Writer out, char key) {
        super(out);
        this.cipher = new CharShiftCipher(key);
    }

    @Override
    public void write(int c) throws IOException {
        out.write(cipher.encrypt((char) c));
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        char[] encrypted = new char[len];
        for (int i = 0; i < len; i++) {
            encrypted[i] = cipher.encrypt(cbuf[off + i]);
        }
        out.write(encrypted, 0, len);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        out.write(cipher.encrypt(str.substring(off, off + len)));
    }
}
