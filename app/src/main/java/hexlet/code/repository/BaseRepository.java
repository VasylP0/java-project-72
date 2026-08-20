package hexlet.code.repository;

import javax.sql.DataSource;

public class BaseRepository {
    protected static DataSource dataSource;

    public static void setDataSource(DataSource dataSource) {
        BaseRepository.dataSource = dataSource;
    }
}
