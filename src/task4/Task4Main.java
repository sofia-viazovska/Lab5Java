package task4;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.net.http.HttpTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** Консольний запуск завдання 4: URL сторінки вводиться з клавіатури. */
public class Task4Main {

    public static final String DEFAULT_URL = "https://www.wikipedia.org/";

    public static void run(Scanner in) {
        System.out.print("URL сторінки [Enter = " + DEFAULT_URL + "]: ");
        String url = in.nextLine().trim();
        if (url.isEmpty()) {
            url = DEFAULT_URL;
        }
        try {
            String html = TagCounter.fetch(url);
            Map<String, Integer> counts = TagCounter.countTags(html);
            if (counts.isEmpty()) {
                System.out.println("На сторінці не знайдено жодного тега.");
                return;
            }
            System.out.println("\n(а) Теги за алфавітом:");
            print(TagCounter.sortedByTag(counts));
            System.out.println("\n(б) Теги за зростанням частоти:");
            print(TagCounter.sortedByFrequency(counts));
        } catch (IllegalArgumentException e) {
            System.out.println("Некоректна адреса: " + e.getMessage());
        } catch (HttpTimeoutException e) {
            System.out.println("Сервер не відповів вчасно.");
        } catch (UnknownHostException e) {
            System.out.println("Невідома адреса сервера (перевірте URL та підключення до мережі).");
        } catch (ConnectException e) {
            System.out.println("Не вдалося підключитися до сервера.");
        } catch (IOException e) {
            String reason = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            System.out.println("Не вдалося завантажити сторінку: " + reason);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Завантаження перервано.");
        }
    }

    private static void print(List<TagCounter.TagStat> stats) {
        for (TagCounter.TagStat s : stats) {
            System.out.printf("  %-14s %d%n", s.tag(), s.count());
        }
    }

    public static void main(String[] args) {
        run(new Scanner(System.in));
    }
}
