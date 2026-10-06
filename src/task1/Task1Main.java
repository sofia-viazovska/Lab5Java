package task1;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Scanner;

/** Консольний запуск завдання 1: шлях до файлу вводиться з клавіатури. */
public class Task1Main {

    public static final String DEFAULT_FILE = "data/sample.txt";

    public static void run(Scanner in) {
        System.out.print("Шлях до текстового файлу [Enter = " + DEFAULT_FILE + "]: ");
        String input = in.nextLine().trim();
        if (input.isEmpty()) {
            input = DEFAULT_FILE;
        }
        try {
            MaxWordsLine.Result r = MaxWordsLine.findLine(Path.of(input));
            System.out.println("Рядок з максимальною кількістю слів:");
            System.out.println("  № рядка : " + r.lineNumber());
            System.out.println("  слів    : " + r.wordCount());
            System.out.println("  текст   : " + r.line());
        } catch (InvalidPathException e) {
            System.out.println("Некоректний шлях: " + e.getMessage());
        } catch (NoSuchFileException e) {
            System.out.println("Файл не знайдено: " + e.getFile());
        } catch (MalformedInputException e) {
            System.out.println("Файл не є текстом у кодуванні UTF-8.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Помилка читання файлу: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        run(new Scanner(System.in));
    }
}
