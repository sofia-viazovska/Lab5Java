package task3;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Шифрування/розшифрування цілих файлів (UTF-8) через фільтр-потоки. */
public class FileCipher {

    /** Читає source звичайним Reader, а пише в target через EncryptingWriter. */
    public static void encryptFile(Path source, Path target, char key) throws IOException {
        try (Reader in = Files.newBufferedReader(source, StandardCharsets.UTF_8);
             Writer out = new EncryptingWriter(Files.newBufferedWriter(target, StandardCharsets.UTF_8), key)) {
            in.transferTo(out);
        }
    }

    /** Читає source через DecryptingReader, а пише в target звичайним Writer. */
    public static void decryptFile(Path source, Path target, char key) throws IOException {
        try (Reader in = new DecryptingReader(Files.newBufferedReader(source, StandardCharsets.UTF_8), key);
             Writer out = Files.newBufferedWriter(target, StandardCharsets.UTF_8)) {
            in.transferTo(out);
        }
    }
}
