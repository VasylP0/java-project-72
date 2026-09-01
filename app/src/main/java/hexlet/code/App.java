package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinJte;
import kong.unirest.core.Unirest;
import org.jsoup.Jsoup;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class App {

    public static HikariDataSource getDataSource() {
        var databaseUrl = System.getenv("JDBC_DATABASE_URL");

        if (databaseUrl == null) {
            databaseUrl = "jdbc:h2:mem:project";
        }

        var config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);

        return new HikariDataSource(config);
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();

        ResourceCodeResolver codeResolver =
                new ResourceCodeResolver("templates", classLoader);

        return TemplateEngine.create(
                codeResolver,
                ContentType.Html
        );
    }

    private static String getFlash(Context ctx) {
        String flash = ctx.sessionAttribute("flash");

        if (flash != null) {
            ctx.sessionAttribute("flash", null);
        }

        return flash;
    }

    public static Javalin getApp() {
        var dataSource = getDataSource();
        BaseRepository.setDataSource(dataSource);

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement();
             var inputStream = App.class.getResourceAsStream("/schema.sql")) {

            var schema = new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );

            statement.execute(schema);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();

            config.staticFiles.add(
                    "/static",
                    Location.CLASSPATH
            );

            config.fileRenderer(
                    new JavalinJte(createTemplateEngine())
            );

            config.routes.get("/", ctx -> {
                var flash = getFlash(ctx);

                ctx.render(
                        "index.jte",
                        Map.of(
                                "flash",
                                flash == null ? "" : flash
                        )
                );
            });

            config.routes.get("/urls", ctx -> {
                var urls = UrlRepository.getEntities();
                var latestChecks = new HashMap<Long, UrlCheck>();

                for (var url : urls) {
                    var latestCheck =
                            UrlCheckRepository.findLatestByUrlId(
                                    url.getId()
                            );

                    latestCheck.ifPresent(
                            check -> latestChecks.put(
                                    url.getId(),
                                    check
                            )
                    );
                }

                var flash = getFlash(ctx);

                ctx.render(
                        "urls/index.jte",
                        Map.of(
                                "urls", urls,
                                "latestChecks", latestChecks,
                                "flash", flash == null ? "" : flash
                        )
                );
            });

            config.routes.post("/urls", ctx -> {
                var url = ctx.formParam("url");

                var normalizedUrl = new URI(url)
                        .resolve("/")
                        .toString()
                        .replaceAll("/$", "");

                var existingUrl =
                        UrlRepository.findByName(normalizedUrl);

                if (existingUrl.isPresent()) {
                    ctx.sessionAttribute(
                            "flash",
                            "Страница уже существует"
                    );

                    ctx.redirect(
                            "/urls/" + existingUrl.get().getId()
                    );

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

                ctx.redirect(
                        "/urls/" + savedUrl.getId()
                );
            });

            config.routes.get("/urls/{id}", ctx -> {
                var id = Long.parseLong(
                        ctx.pathParam("id")
                );

                var url = UrlRepository
                        .find(id)
                        .orElseThrow();

                var checks =
                        UrlCheckRepository.getByUrlId(id);

                var flash = getFlash(ctx);

                ctx.render(
                        "urls/show.jte",
                        Map.of(
                                "url", url,
                                "checks", checks,
                                "flash", flash == null ? "" : flash
                        )
                );
            });

            config.routes.post("/urls/{id}/checks", ctx -> {
                var id = Long.parseLong(
                        ctx.pathParam("id")
                );

                var url = UrlRepository
                        .find(id)
                        .orElseThrow();

                try {
                    var response = Unirest
                            .get(url.getName())
                            .asString();

                    var statusCode = response.getStatus();

                    if (statusCode >= 400) {
                        ctx.sessionAttribute(
                                "flash",
                                "Произошла ошибка при проверке"
                        );

                        ctx.redirect("/urls/" + id);
                        return;
                    }

                    var document = Jsoup.parse(
                            response.getBody()
                    );

                    var title = document.title();

                    var h1Element =
                            document.selectFirst("h1");

                    var h1 = h1Element == null
                            ? null
                            : h1Element.text();

                    var descriptionElement =
                            document.selectFirst(
                                    "meta[name=description]"
                            );

                    var description =
                            descriptionElement == null
                                    ? null
                                    : descriptionElement.attr(
                                    "content"
                            );

                    var check = new UrlCheck(
                            id,
                            statusCode
                    );

                    check.setTitle(title);
                    check.setH1(h1);
                    check.setDescription(description);

                    UrlCheckRepository.save(check);

                    ctx.sessionAttribute(
                            "flash",
                            "Страница успешно проверена"
                    );

                } catch (Exception e) {
                    ctx.sessionAttribute(
                            "flash",
                            "Произошла ошибка при проверке"
                    );
                }

                ctx.redirect("/urls/" + id);
            });
        });
    }

    public static void main(String[] args) {
        var app = getApp();

        var port = System.getenv("PORT");

        if (port != null) {
            app.start(Integer.parseInt(port));
        } else {
            app.start(7070);
        }
    }
}