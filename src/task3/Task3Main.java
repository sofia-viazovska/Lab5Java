package task3;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.MalformedInputException;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Scanner;

/** Консольний запуск завдання 3: текст, ключ і шляхи до файлів вводяться з клавіатури. */
public class Task3Main {

    public static void run(Scanner in) {
        while (true) {
            System.out.println("\n--- Завдання 3: шифрування потоку символів ---");
            System.out.println("1. Зашифрувати/розшифрувати рядок");
            System.out.println("2. Зашифрувати файл");
            System.out.println("3. Розшифрувати файл");
            System.out.println("0. Назад");
            System.out.print("Ваш вибір: ");
            if (!in.hasNextLine()) {
                return;
            }
            String choice = in.nextLine().trim();
            switch (choice) {
                case "1" -> textDemo(in);
                case "2" -> fileMode(in, true);
                case "3" -> fileMode(in, false);
                case "0" -> {
                    return;
                }
                default -> System.out.println("Невідомий пункт меню.");
            }
        }
    }

    private static Character readKey(Scanner in) {
        System.out.print("Символ-ключ (один символ): ");
        String s = in.nextLine();
        if (s.length() != 1) {
            System.out.println("Потрібно ввести рівно один символ.");
            return null;
        }
        if (Character.isSurrogate(s.charAt(0))) {
            System.out.println("Цей символ не підходить як ключ.");
            return null;
        }
        return s.charAt(0);
    }

    private static void textDemo(Scanner in) {
        Character key = readKey(in);
        if (key == null) {
            return;
        }
        System.out.print("Текст: ");
        String text = in.nextLine();
        try {
            // шифруємо через EncryptingWriter, розшифровуємо через DecryptingReader
            StringWriter sink = new StringWriter();
            try (Writer w = new EncryptingWriter(sink, key)) {
                w.write(text);
            }
            String encrypted = sink.toString();

            StringBuilder decrypted = new StringBuilder();
            try (var r = new DecryptingReader(new StringReader(encrypted), key)) {
                int c;
                while ((c = r.read()) != -1) {
                    decrypted.append((char) c);
                }
            }
            System.out.println("Зашифровано  : " + visible(encrypted));
            System.out.println("Розшифровано : " + decrypted);
            System.out.println(decrypted.toString().equals(text) ? "Збігається з оригіналом." : "ПОМИЛКА: не збігається!");
        } catch (IOException e) {
            System.out.println("Помилка потоку: " + e.getMessage());
        }
    }

    private static void fileMode(Scanner in, boolean encrypt) {
        Character key = readKey(in);
        if (key == null) {
            return;
        }
        System.out.print("Вхідний файл: ");
        String src = in.nextLine().trim();
        System.out.print("Вихідний файл: ");
        String dst = in.nextLine().trim();
        try {
            Path source = Path.of(src);
            Path target = Path.of(dst);
            if (encrypt) {
                FileCipher.encryptFile(source, target, key);
            } else {
                FileCipher.decryptFile(source, target, key);
            }
            System.out.println("Готово: " + target);
        } catch (InvalidPathException e) {
            System.out.println("Некоректний шлях: " + e.getMessage());
        } catch (NoSuchFileException e) {
            System.out.println("Файл не знайдено: " + e.getFile());
        } catch (MalformedInputException e) {
            System.out.println("Вхідний файл не є текстом у кодуванні UTF-8.");
        } catch (IOException e) {
            System.out.println("Помилка файлу: " + e.getMessage());
        }
    }

    /** Керівні й невидимі символи показуємо як \\uXXXX, щоб вивід не ламав консоль. */
    static String visible(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isISOControl(c) || !Character.isDefined(c) || Character.isSurrogate(c)
                    || Character.getType(c) == Character.PRIVATE_USE) {
                sb.append(String.format("\\u%04X", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        run(new Scanner(System.in));
    }
}
