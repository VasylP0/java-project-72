package hexlet.code;

import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {

    private Javalin app;

    @BeforeEach
    void setUp() throws Exception {
        app = App.getApp();
        UrlCheckRepository.clear();
        UrlRepository.clear();
    }

    @Test
    void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");

            assertEquals(
                    HttpStatus.OK.getCode(),
                    response.code()
            );
            assertTrue(
                    response.body()
                            .string()
                            .contains("Анализатор страниц")
            );
        });
    }

    @Test
    void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");

            assertEquals(
                    HttpStatus.OK.getCode(),
                    response.code()
            );
            assertTrue(
                    response.body()
                            .string()
                            .contains("Сайты")
            );
        });
    }

    @Test
    void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post(
                    "/urls",
                    "url=https://example.com"
            );

            assertEquals(
                    HttpStatus.FOUND.getCode(),
                    response.code()
            );

            var savedUrl = UrlRepository
                    .findByName("https://example.com")
                    .orElseThrow();

            var pageResponse = client.get(
                    "/urls/" + savedUrl.getId()
            );

            assertEquals(
                    HttpStatus.OK.getCode(),
                    pageResponse.code()
            );
            assertTrue(
                    pageResponse.body()
                            .string()
                            .contains("https://example.com")
            );
        });
    }

    @Test
    void testExistingUrl() {
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

            assertEquals(
                    HttpStatus.FOUND.getCode(),
                    response.code()
            );

            var pageResponse = client.get(
                    "/urls/" + savedUrl.getId()
            );

            assertEquals(
                    HttpStatus.OK.getCode(),
                    pageResponse.code()
            );

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

            assertEquals(
                    HttpStatus.OK.getCode(),
                    response.code()
            );
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
                            .setResponseCode(
                                    HttpStatus.OK.getCode()
                            )
                            .setHeader(
                                    "Content-Type",
                                    "text/html"
                            )
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

            JavalinTest.test(app, (server, client) -> {
                var createResponse = client.post(
                        "/urls",
                        "url=" + mockUrl
                );

                assertEquals(
                        HttpStatus.FOUND.getCode(),
                        createResponse.code()
                );

                var savedUrl = UrlRepository
                        .findByName(normalizedMockUrl)
                        .orElseThrow();

                var checkResponse = client.post(
                        "/urls/" + savedUrl.getId() + "/checks",
                        ""
                );

                assertEquals(
                        HttpStatus.FOUND.getCode(),
                        checkResponse.code()
                );

                var check = UrlCheckRepository
                        .findLatestByUrlId(savedUrl.getId())
                        .orElseThrow();

                assertEquals(
                        HttpStatus.OK.getCode(),
                        check.getStatusCode()
                );
                assertEquals(
                        "Test title",
                        check.getTitle()
                );
                assertEquals(
                        "Test heading",
                        check.getH1()
                );
                assertEquals(
                        "Test description",
                        check.getDescription()
                );
            });
        }
    }
}