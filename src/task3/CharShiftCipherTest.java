package task3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;

public class CharShiftCipherTest {

    @Test
    public void shiftsCodeByKeyCode() {
        CharShiftCipher c = new CharShiftCipher('\u0003');
        assertEquals('d', c.encrypt('a'));
        assertEquals('a', c.decrypt('d'));
        CharShiftCipher k = new CharShiftCipher('A'); // код 65
        assertEquals((char) ('a' + 65), k.encrypt('a'));
    }

    @Test
    public void roundTripForEveryNonSurrogateChar() {
        for (char key : new char[]{'a', 'Я', '\u0001', '￿', '퟿', ''}) {
            CharShiftCipher c = new CharShiftCipher(key);
            for (int i = 0; i <= 0xFFFF; i++) {
                char ch = (char) i;
                assertEquals("key=" + (int) key + " ch=" + i, ch, c.decrypt(c.encrypt(ch)));
                if (!Character.isSurrogate(ch)) {
                    assertEquals(false, Character.isSurrogate(c.encrypt(ch)));
                }
            }
        }
    }

    @Test
    public void wrapsAroundAtEndOfRange() {
        CharShiftCipher c = new CharShiftCipher('\u0002');
        assertEquals('\u0000', c.encrypt('￾'));
        assertEquals('\u0001', c.encrypt('￿'));
    }

    @Test
    public void surrogatesAreLeftUntouched() {
        CharShiftCipher c = new CharShiftCipher('x');
        String text = "ok 😀 ok";
        assertEquals(text, c.decrypt(c.encrypt(text)));
        assertEquals('\uD83D', c.encrypt('\uD83D'));
    }

    @Test
    public void surrogateKeyIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new CharShiftCipher('\uD800'));
    }

    @Test
    public void stringRoundTrip() {
        CharShiftCipher c = new CharShiftCipher('Ж');
        String text = "Привіт, world! 123 ґєії\nнаступний рядок\t";
        String enc = c.encrypt(text);
        assertNotEquals(text, enc);
        assertEquals(text, c.decrypt(enc));
    }

    @Test
    public void filterWriterAndReaderRoundTrip() throws IOException {
        String text = "Фільтр-потоки: write(int), write(char[]), write(String)";
        StringWriter sink = new StringWriter();
        try (Writer w = new EncryptingWriter(sink, 'k')) {
            w.write(text.charAt(0));                       // write(int)
            w.write(text.toCharArray(), 1, 5);             // write(char[],int,int)
            w.write(text, 6, text.length() - 6);           // write(String,int,int)
        }
        String enc = sink.toString();
        assertEquals(new CharShiftCipher('k').encrypt(text), enc);

        StringBuilder sb = new StringBuilder();
        try (DecryptingReader r = new DecryptingReader(new StringReader(enc), 'k')) {
            int c = r.read();                              // read()
            sb.append((char) c);
            char[] buf = new char[7];
            int n;
            while ((n = r.read(buf, 0, buf.length)) != -1) { // read(char[],int,int)
                sb.append(buf, 0, n);
            }
        }
        assertEquals(text, sb.toString());
    }

    @Test
    public void fileRoundTrip() throws IOException {
        Path plain = Files.createTempFile("plain", ".txt");
        Path enc = Files.createTempFile("enc", ".txt");
        Path dec = Files.createTempFile("dec", ".txt");
        for (Path p : new Path[]{plain, enc, dec}) {
            p.toFile().deleteOnExit();
        }
        String text = "Перший рядок\nДругий рядок з емодзі 😀\nthird line\n";
        Files.writeString(plain, text, StandardCharsets.UTF_8);

        FileCipher.encryptFile(plain, enc, 'Q');
        assertNotEquals(text, Files.readString(enc, StandardCharsets.UTF_8));
        FileCipher.decryptFile(enc, dec, 'Q');
        assertEquals(text, Files.readString(dec, StandardCharsets.UTF_8));
    }
}
