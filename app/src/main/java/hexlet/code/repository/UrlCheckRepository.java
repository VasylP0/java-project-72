package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlCheckRepository extends BaseRepository {

    public static void save(UrlCheck check) throws SQLException {
        var sql = """
                INSERT INTO url_checks
                (url_id, status_code, h1, title, description, created_at)
                VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """;

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setLong(1, check.getUrlId());
            statement.setInt(2, check.getStatusCode());
            statement.setString(3, check.getH1());
            statement.setString(4, check.getTitle());
            statement.setString(5, check.getDescription());

            statement.executeUpdate();
        }
    }

    public static List<UrlCheck> getByUrlId(Long urlId) throws SQLException {
        var checks = new ArrayList<UrlCheck>();

        var sql = """
                SELECT *
                FROM url_checks
                WHERE url_id = ?
                ORDER BY created_at DESC
                """;

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setLong(1, urlId);

            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    var check = new UrlCheck(
                            resultSet.getLong("url_id"),
                            resultSet.getInt("status_code")
                    );

                    check.setId(resultSet.getLong("id"));
                    check.setH1(resultSet.getString("h1"));
                    check.setTitle(resultSet.getString("title"));
                    check.setDescription(resultSet.getString("description"));
                    check.setCreatedAt(resultSet.getTimestamp("created_at"));

                    checks.add(check);
                }
            }
        }

        return checks;
    }

    public static Optional<UrlCheck> findLatestByUrlId(Long urlId)
            throws SQLException {

        var sql = """
                SELECT *
                FROM url_checks
                WHERE url_id = ?
                ORDER BY created_at DESC
                LIMIT 1
                """;

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setLong(1, urlId);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var check = new UrlCheck(
                            resultSet.getLong("url_id"),
                            resultSet.getInt("status_code")
                    );

                    check.setId(resultSet.getLong("id"));
                    check.setH1(resultSet.getString("h1"));
                    check.setTitle(resultSet.getString("title"));
                    check.setDescription(resultSet.getString("description"));
                    check.setCreatedAt(resultSet.getTimestamp("created_at"));

                    return Optional.of(check);
                }
            }
        }

        return Optional.empty();
    }
}