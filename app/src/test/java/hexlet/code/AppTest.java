package hexlet.code;

import hexlet.code.repository.UrlRepository;
import io.javalin.testtools.JavalinTest;
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
}