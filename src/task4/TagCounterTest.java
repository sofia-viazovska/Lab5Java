package task4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class TagCounterTest {

    private static final String HTML = """
            <!DOCTYPE html>
            <HTML><head><title>T</title>
            <style> a > b { color: red } </style>
            <script>if (1 <b && 2 <c) { document.write("<div>"); }</script>
            </head>
            <body>
              <!-- <p>коментар</p> -->
              <div class="x"><p>one</p><p>two</p><br/><P>three</P></div>
              <a href="#">link</a> <A href="#">link2</A>
            </body></HTML>
            """;

    @Test
    public void countsOpeningTagsCaseInsensitively() {
        Map<String, Integer> c = TagCounter.countTags(HTML);
        assertEquals(Integer.valueOf(1), c.get("html"));
        assertEquals(Integer.valueOf(3), c.get("p"));
        assertEquals(Integer.valueOf(2), c.get("a"));
        assertEquals(Integer.valueOf(1), c.get("br"));
        assertEquals(Integer.valueOf(1), c.get("div"));
    }

    @Test
    public void ignoresCommentsScriptAndStyleContent() {
        Map<String, Integer> c = TagCounter.countTags(HTML);
        assertEquals(Integer.valueOf(1), c.get("script"));
        assertEquals(Integer.valueOf(1), c.get("style"));
        assertEquals(null, c.get("b"));
        assertEquals(null, c.get("c"));
    }

    @Test
    public void sortedByTagIsLexicographic() {
        List<TagCounter.TagStat> s = TagCounter.sortedByTag(TagCounter.countTags(HTML));
        List<String> names = s.stream().map(TagCounter.TagStat::tag).toList();
        assertEquals(names.stream().sorted().toList(), names);
        assertEquals("a", names.get(0));
    }

    @Test
    public void sortedByFrequencyIsAscendingWithTagTieBreak() {
        List<TagCounter.TagStat> s = TagCounter.sortedByFrequency(TagCounter.countTags(HTML));
        for (int i = 1; i < s.size(); i++) {
            TagCounter.TagStat prev = s.get(i - 1);
            TagCounter.TagStat cur = s.get(i);
            boolean ok = prev.count() < cur.count()
                    || (prev.count() == cur.count() && prev.tag().compareTo(cur.tag()) < 0);
            assertEquals(prev + " / " + cur, true, ok);
        }
        assertEquals("p", s.get(s.size() - 1).tag());
    }

    @Test
    public void emptyAndTaglessInput() {
        assertEquals(0, TagCounter.countTags("").size());
        assertEquals(0, TagCounter.countTags("a < b and 3 <4 plain text").size());
    }

    @Test
    public void rejectsBadUrls() {
        assertThrows(IllegalArgumentException.class, () -> TagCounter.fetch("not a url"));
        assertThrows(IllegalArgumentException.class, () -> TagCounter.fetch("ftp://example.com/x"));
        assertThrows(IllegalArgumentException.class, () -> TagCounter.fetch("example.com"));
    }

    @Test
    public void fetchesPageFromLocalServer() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/ok", ex -> {
            byte[] body = "<html><body><p>a</p><p>b</p></body></html>".getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
            ex.sendResponseHeaders(200, body.length);
            try (OutputStream os = ex.getResponseBody()) {
                os.write(body);
            }
        });
        server.createContext("/missing", ex -> {
            ex.sendResponseHeaders(404, -1);
            ex.close();
        });
        server.start();
        try {
            String base = "http://127.0.0.1:" + server.getAddress().getPort();
            Map<String, Integer> c = TagCounter.countTags(TagCounter.fetch(base + "/ok"));
            assertEquals(Integer.valueOf(2), c.get("p"));
            assertThrows(IOException.class, () -> TagCounter.fetch(base + "/missing"));
        } finally {
            server.stop(0);
        }
    }
}
