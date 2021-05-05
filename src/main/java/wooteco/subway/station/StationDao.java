package wooteco.subway.station;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> new Station(
        resultSet.getLong("id"),
        resultSet.getString("name")
    );

    public StationDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(final Station station) {
        final String sql = "INSERT INTO station (name) VALUES (?)";
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        final PreparedStatementCreator preparedStatementCreator = con -> {
            final PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, station.getName());
            return preparedStatement;
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        final long id = keyHolder.getKey().longValue();
        return new Station(id, station.getName());
    }

    public void deleteById(final long id) {
        final String sql = "DELETE FROM station WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Station> findAll() {
        final String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public Optional<Station> findById(final Long id) {
        final String sql = "SELECT * FROM station WHERE id = ?";
        final List<Station> stations = jdbcTemplate.query(sql, stationRowMapper, id);
        if (stations.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(stations.get(0));
    }

    public Optional<Station> findByName(final String name) {
        final String sql = "SELECT * FROM station WHERE name = ?";
        final List<Station> stations = jdbcTemplate.query(sql, stationRowMapper, name);
        if (stations.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(stations.get(0));
    }
}
