package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.UrlController;
import hexlet.code.repository.BaseRepository;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinJte;

import java.nio.charset.StandardCharsets;

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
        var classLoader = App.class.getClassLoader();
        var codeResolver = new ResourceCodeResolver("templates", classLoader);

        return TemplateEngine.create(codeResolver, ContentType.Html);
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

            config.staticFiles.add("/static", Location.CLASSPATH);

            config.fileRenderer(
                    new JavalinJte(createTemplateEngine())
            );

            config.routes.get("/", UrlController::home);
            config.routes.get("/urls", UrlController::index);
            config.routes.post("/urls", UrlController::create);
            config.routes.get("/urls/{id}", UrlController::show);
            config.routes.post("/urls/{id}/checks", UrlController::check);
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