package task1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import org.junit.Test;

public class MaxWordsLineTest {

    private static Path write(String content) throws IOException {
        Path f = Files.createTempFile("words", ".txt");
        f.toFile().deleteOnExit();
        Files.writeString(f, content, StandardCharsets.UTF_8);
        return f;
    }

    @Test
    public void countsWordsIgnoringPunctuation() {
        assertEquals(4, MaxWordsLine.countWords("  Hello,   world! ... one-two  три "));
        assertEquals(2, MaxWordsLine.countWords("м'яч — гра"));
        assertEquals(0, MaxWordsLine.countWords(" ,.;  "));
        assertEquals(0, MaxWordsLine.countWords(""));
    }

    @Test
    public void findsLineWithMostWords() throws IOException {
        MaxWordsLine.Result r = MaxWordsLine.findLine(write("a b\nа б в г ґ\n\nx y z\n"));
        assertEquals(2, r.lineNumber());
        assertEquals(5, r.wordCount());
        assertEquals("а б в г ґ", r.line());
    }

    @Test
    public void tieReturnsFirstLine() throws IOException {
        MaxWordsLine.Result r = MaxWordsLine.findLine(write("a b\nc d\n"));
        assertEquals(1, r.lineNumber());
    }

    @Test
    public void fileWithOnlyBlankLinesStillReturnsALine() throws IOException {
        MaxWordsLine.Result r = MaxWordsLine.findLine(write("\n\n"));
        assertEquals(0, r.wordCount());
    }

    @Test
    public void emptyFileIsRejected() throws IOException {
        Path f = write("");
        assertThrows(IllegalArgumentException.class, () -> MaxWordsLine.findLine(f));
    }

    @Test
    public void missingFileThrowsNoSuchFile() {
        assertThrows(NoSuchFileException.class, () -> MaxWordsLine.findLine(Path.of("no/such/file.txt")));
    }
}
