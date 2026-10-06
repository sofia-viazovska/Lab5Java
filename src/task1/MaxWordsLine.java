package task1;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Завдання 1: знайти в текстовому файлі рядок з максимальною кількістю слів.
 *
 * Слово - послідовність літер/цифр, всередині якої можуть бути апостроф або дефіс
 * ("м'яч", "по-новому"). Знаки пунктуації й пробіли словами не рахуються.
 * Якщо кілька рядків мають однакову (максимальну) кількість слів - повертається перший.
 */
public class MaxWordsLine {

    private static final Pattern WORD =
            Pattern.compile("[\\p{L}\\p{N}]+(?:['’ʼ-][\\p{L}\\p{N}]+)*");

    /** Результат пошуку: номер рядка (з 1), сам рядок і кількість слів у ньому. */
    public record Result(int lineNumber, String line, int wordCount) {
    }

    /** Кількість слів у рядку. */
    public static int countWords(String line) {
        Matcher m = WORD.matcher(line);
        int count = 0;
        while (m.find()) {
            count++;
        }
        return count;
    }

    /**
     * Читає файл (UTF-8) рядок за рядком і повертає рядок з максимальною кількістю слів.
     *
     * @throws IOException              файл не існує / не читається / не є текстом UTF-8
     * @throws IllegalArgumentException файл порожній (немає жодного рядка)
     */
    public static Result findLine(Path file) throws IOException {
        Result best = null;
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int number = 0;
            while ((line = reader.readLine()) != null) {
                number++;
                int words = countWords(line);
                if (best == null || words > best.wordCount()) {
                    best = new Result(number, line, words);
                }
            }
        }
        if (best == null) {
            throw new IllegalArgumentException("Файл порожній: " + file);
        }
        return best;
    }
}
