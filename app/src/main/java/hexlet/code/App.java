package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.repository.BaseRepository;
import io.javalin.Javalin;

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
            config.routes.get("/", ctx -> ctx.result("Hello World"));
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
