package hexlet.code.repository;

import hexlet.code.model.Url;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlRepository extends BaseRepository {

    public static void save(Url url) throws SQLException {
        var sql = "INSERT INTO urls (name, created_at) VALUES (?, CURRENT_TIMESTAMP)";

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setString(1, url.getName());
            statement.executeUpdate();
        }
    }

    public static List<Url> getEntities() throws SQLException {
        var urls = new ArrayList<Url>();

        var sql = "SELECT * FROM urls ORDER BY created_at DESC";

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql);
             var resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                var url = new Url(resultSet.getString("name"));

                url.setId(resultSet.getLong("id"));
                url.setCreatedAt(resultSet.getTimestamp("created_at"));

                urls.add(url);
            }
        }

        return urls;
    }

    public static Optional<Url> findByName(String name) throws SQLException {
        var sql = "SELECT * FROM urls WHERE name = ?";

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var url = new Url(resultSet.getString("name"));

                    url.setId(resultSet.getLong("id"));
                    url.setCreatedAt(resultSet.getTimestamp("created_at"));

                    return Optional.of(url);
                }
            }
        }

        return Optional.empty();
    }

    public static Optional<Url> find(Long id) throws SQLException {
        var sql = "SELECT * FROM urls WHERE id = ?";

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var url = new Url(resultSet.getString("name"));

                    url.setId(resultSet.getLong("id"));
                    url.setCreatedAt(resultSet.getTimestamp("created_at"));

                    return Optional.of(url);
                }
            }
        }

        return Optional.empty();
    }
}