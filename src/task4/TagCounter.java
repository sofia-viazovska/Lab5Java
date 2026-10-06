package task4;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Завдання 4: підрахунок частоти тегів на веб-сторінці.
 *
 * Рахуються відкриваючі теги (<div ...>, <br/>), закриваючі (</div>) окремо не
 * враховуються, щоб кожен елемент потрапляв у статистику один раз. Назви тегів
 * приводяться до нижнього регістру. Коментарі та вміст <script>/<style> до
 * підрахунку не потрапляють - там "<" може бути просто частиною коду.
 */
public class TagCounter {

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);
    private static final Pattern RAW_TEXT_BLOCK =
            Pattern.compile("(<(script|style)\\b[^>]*>).*?(</\\2\\s*>)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final Pattern OPEN_TAG = Pattern.compile("<([a-zA-Z][a-zA-Z0-9:_-]*)");

    /** Один запис статистики: тег і скільки разів він зустрівся. */
    public record TagStat(String tag, int count) {
    }

    /** Завантажує сторінку за URL (http/https) і повертає її HTML. */
    public static String fetch(String url) throws IOException, InterruptedException {
        URI uri = URI.create(url.trim());          // IllegalArgumentException на некоректний URL
        String scheme = uri.getScheme();
        if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                || uri.getHost() == null) {
            throw new IllegalArgumentException("Потрібна адреса виду http://... або https://...");
        }
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(20))
                .header("User-Agent", "Mozilla/5.0 (Lab5 TagCounter)")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Сервер відповів кодом " + response.statusCode());
        }
        return response.body();
    }

    /** Рахує теги в HTML. Результат - TreeMap, тобто вже відсортований за назвою тега. */
    public static TreeMap<String, Integer> countTags(String html) {
        String cleaned = COMMENT.matcher(html).replaceAll("");
        cleaned = RAW_TEXT_BLOCK.matcher(cleaned).replaceAll("$1$3");

        TreeMap<String, Integer> counts = new TreeMap<>();
        Matcher m = OPEN_TAG.matcher(cleaned);
        while (m.find()) {
            counts.merge(m.group(1).toLowerCase(Locale.ROOT), 1, Integer::sum);
        }
        return counts;
    }

    /** (a) Теги в лексикографічному порядку за назвою. */
    public static List<TagStat> sortedByTag(Map<String, Integer> counts) {
        List<TagStat> list = new ArrayList<>();
        counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> list.add(new TagStat(e.getKey(), e.getValue())));
        return list;
    }

    /** (b) Теги за зростанням частоти; при рівній частоті - за назвою. */
    public static List<TagStat> sortedByFrequency(Map<String, Integer> counts) {
        List<TagStat> list = new ArrayList<>();
        counts.forEach((tag, n) -> list.add(new TagStat(tag, n)));
        list.sort((a, b) -> a.count() != b.count()
                ? Integer.compare(a.count(), b.count())
                : a.tag().compareTo(b.tag()));
        return list;
    }
}
