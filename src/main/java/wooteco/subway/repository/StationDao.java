package wooteco.subway.repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.station.Station;

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
        String saveQuery = "INSERT INTO Station (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = (connection) -> {
            PreparedStatement prepareStatement = connection
                .prepareStatement(saveQuery, new String[]{"id"});
            prepareStatement.setString(1, station.getName());
            return prepareStatement;
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public List<Station> findAll() {
        String query = "SELECT * FROM STATION";
        return jdbcTemplate.query(query, ROW_MAPPER);
    }

    public Optional<Station> findById(Long id) {
        String query = "SELECT * FROM STATION WHERE ID = ?";
        return jdbcTemplate.query(query, ROW_MAPPER, id)
            .stream()
            .findAny();
    }

    public void deleteById(Long id) {
        String query = "DELETE FROM STATION WHERE ID = ?";
        jdbcTemplate.update(query, id);
    }
}
