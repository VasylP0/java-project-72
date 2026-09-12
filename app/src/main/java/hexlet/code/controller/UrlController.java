package hexlet.code.controller;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import kong.unirest.core.Unirest;
import org.jsoup.Jsoup;

import java.net.URI;
import java.util.Map;

public class UrlController {

    private static String getFlash(Context ctx) {
        String flash = ctx.sessionAttribute("flash");

        if (flash != null) {
            ctx.sessionAttribute("flash", null);
        }

        return flash;
    }

    public static void home(Context ctx) {
        var flash = getFlash(ctx);

        ctx.render(
                "index.jte",
                Map.of("flash", flash == null ? "" : flash)
        );
    }

    public static void index(Context ctx) throws Exception {
        var urls = UrlRepository.getEntities();
        var latestChecks = UrlCheckRepository.getLatestChecks();

        var flash = getFlash(ctx);

        ctx.render(
                "urls/index.jte",
                Map.of(
                        "urls", urls,
                        "latestChecks", latestChecks,
                        "flash", flash == null ? "" : flash
                )
        );
    }

    public static void create(Context ctx) throws Exception {
        var inputUrl = ctx.formParam("url");
        URI uri;

        try {
            uri = new URI(inputUrl);
        } catch (Exception e) {
            renderInvalidUrl(ctx);
            return;
        }

        var scheme = uri.getScheme();
        var host = uri.getHost();

        if (scheme == null
                || (!scheme.equals("http") && !scheme.equals("https"))
                || host == null) {
            renderInvalidUrl(ctx);
            return;
        }

        var normalizedUrl = new URI(
                scheme,
                null,
                host,
                uri.getPort(),
                null,
                null,
                null
        ).toString();

        var existingUrl = UrlRepository.findByName(normalizedUrl);

        if (existingUrl.isPresent()) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.redirect("/urls/" + existingUrl.get().getId());
            return;
        }

        var newUrl = new Url(normalizedUrl);
        UrlRepository.save(newUrl);

        var savedUrl = UrlRepository
                .findByName(normalizedUrl)
                .orElseThrow();

        ctx.sessionAttribute(
                "flash",
                "Страница успешно добавлена"
        );

        ctx.redirect("/urls/" + savedUrl.getId());
    }

    private static void renderInvalidUrl(Context ctx) {
        ctx.status(HttpStatus.UNPROCESSABLE_CONTENT);

        ctx.render(
                "index.jte",
                Map.of(
                        "flash",
                        "Некорректный URL"
                )
        );
    }

    public static void show(Context ctx) throws Exception {
        var id = Long.parseLong(ctx.pathParam("id"));

        var url = UrlRepository
                .find(id)
                .orElseThrow();

        var checks = UrlCheckRepository.getByUrlId(id);
        var flash = getFlash(ctx);

        ctx.render(
                "urls/show.jte",
                Map.of(
                        "url", url,
                        "checks", checks,
                        "flash", flash == null ? "" : flash
                )
        );
    }

    public static void check(Context ctx) throws Exception {
        var id = Long.parseLong(ctx.pathParam("id"));

        var url = UrlRepository
                .find(id)
                .orElseThrow();

        try {
            var response = Unirest
                    .get(url.getName())
                    .asString();

            var statusCode = response.getStatus();

            if (statusCode >= HttpStatus.BAD_REQUEST.getCode()) {
                ctx.sessionAttribute(
                        "flash",
                        "Произошла ошибка при проверке"
                );

                ctx.redirect("/urls/" + id);
                return;
            }

            var document = Jsoup.parse(response.getBody());
            var title = document.title();

            var h1Element = document.selectFirst("h1");
            var h1 = h1Element == null
                    ? null
                    : h1Element.text();

            var descriptionElement = document.selectFirst(
                    "meta[name=description]"
            );

            var description = descriptionElement == null
                    ? null
                    : descriptionElement.attr("content");

            var check = new UrlCheck(id, statusCode);

            check.setTitle(title);
            check.setH1(h1);
            check.setDescription(description);

            UrlCheckRepository.save(check);

            ctx.sessionAttribute(
                    "flash",
                    "Страница успешно проверена"
            );

        } catch (Exception e) {
            e.printStackTrace();

            ctx.sessionAttribute(
                    "flash",
                    "Произошла ошибка при проверке"
            );
        }

        ctx.redirect("/urls/" + id);
    }
}