package hexlet.code;

import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {

    @Test
    void testMainPage() {
        var app = App.getApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");

            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("Анализатор страниц"));
        });
    }

    @Test
    void testUrlsPage() {
        var app = App.getApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");

            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("Сайты"));
        });
    }

    @Test
    void testCreateUrl() {
        var app = App.getApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.post(
                    "/urls",
                    "url=https://example.com"
            );

            assertEquals(302, response.code());

            var savedUrl = UrlRepository
                    .findByName("https://example.com")
                    .orElseThrow();

            var pageResponse = client.get(
                    "/urls/" + savedUrl.getId()
            );

            assertEquals(200, pageResponse.code());
            assertTrue(
                    pageResponse.body()
                            .string()
                            .contains("https://example.com")
            );
        });
    }

    @Test
    void testExistingUrl() {
        var app = App.getApp();

        JavalinTest.test(app, (server, client) -> {
            client.post(
                    "/urls",
                    "url=https://example.com"
            );

            var savedUrl = UrlRepository
                    .findByName("https://example.com")
                    .orElseThrow();

            var response = client.post(
                    "/urls",
                    "url=https://example.com"
            );

            assertEquals(302, response.code());

            var pageResponse = client.get(
                    "/urls/" + savedUrl.getId()
            );

            assertEquals(200, pageResponse.code());

            var urls = UrlRepository.getEntities();

            var count = urls.stream()
                    .filter(url ->
                            url.getName()
                                    .equals("https://example.com")
                    )
                    .count();

            assertEquals(1, count);
        });
    }

    @Test
    void testShowUrl() {
        var app = App.getApp();

        JavalinTest.test(app, (server, client) -> {
            client.post(
                    "/urls",
                    "url=https://hexlet.io"
            );

            var savedUrl = UrlRepository
                    .findByName("https://hexlet.io")
                    .orElseThrow();

            var response = client.get(
                    "/urls/" + savedUrl.getId()
            );

            assertEquals(200, response.code());
            assertTrue(
                    response.body()
                            .string()
                            .contains("https://hexlet.io")
            );
        });
    }

    @Test
    void testUrlCheck() throws Exception {
        try (var mockServer = new MockWebServer()) {
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(200)
                            .setHeader("Content-Type", "text/html")
                            .setBody("""
                                    <html>
                                    <head>
                                        <title>Test title</title>
                                        <meta name="description"
                                              content="Test description">
                                    </head>
                                    <body>
                                        <h1>Test heading</h1>
                                    </body>
                                    </html>
                                    """)
            );

            mockServer.start();

            var mockUrl = mockServer.url("/").toString();
            var normalizedMockUrl = mockUrl.substring(
                    0,
                    mockUrl.length() - 1
            );

            var app = App.getApp();

            JavalinTest.test(app, (server, client) -> {
                var createResponse = client.post(
                        "/urls",
                        "url=" + mockUrl
                );

                assertEquals(302, createResponse.code());

                var savedUrl = UrlRepository
                        .findByName(normalizedMockUrl)
                        .orElseThrow();

                var checkResponse = client.post(
                        "/urls/" + savedUrl.getId() + "/checks",
                        ""
                );

                assertEquals(302, checkResponse.code());

                var check = UrlCheckRepository
                        .findLatestByUrlId(savedUrl.getId())
                        .orElseThrow();

                assertEquals(200, check.getStatusCode());
                assertEquals("Test title", check.getTitle());
                assertEquals("Test heading", check.getH1());
                assertEquals(
                        "Test description",
                        check.getDescription()
                );
            });
        }
    }
}