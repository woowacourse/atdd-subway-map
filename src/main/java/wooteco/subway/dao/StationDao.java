package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.station.Station;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class StationDao {
    private static final RowMapper<Station> ROW_MAPPER = (resultSet, rowNumber) -> {
        long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        return new Station(id, name);
    };

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Station station) {
        String query = "INSERT INTO Station (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = (connection) -> {
            PreparedStatement prepareStatement = connection.prepareStatement(query, new String[]{"id"});
            prepareStatement.setString(1, station.getName());
            return prepareStatement;
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public List<Station> findAll() {
        String query = "SELECT ID, NAME FROM STATION";
        return jdbcTemplate.query(query, ROW_MAPPER);
    }

    public Station findById(long id) {
        String query = "SELECT ID, NAME FROM STATION WHERE ID = ?";
        return jdbcTemplate.queryForObject(query, ROW_MAPPER, id);
    }

    public void deleteById(long id) {
        String query = "DELETE FROM STATION WHERE ID = ?";
        jdbcTemplate.update(query, id);
    }
}
