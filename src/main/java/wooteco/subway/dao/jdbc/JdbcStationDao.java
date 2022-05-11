package wooteco.subway.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@Repository
public class JdbcStationDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Station> STATION_ROW_MAPPER = (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    @Override
    public Long save(Station station) {
        String sql = "INSERT INTO station (name) values (?)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            final PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, station.getName());
            return statement;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public Optional<Station> findById(Long id) {
        String sql = "SELECT * FROM station WHERE id = ?";
        try {
            final Station station = jdbcTemplate.queryForObject(sql, STATION_ROW_MAPPER, id);
            return Optional.of(station);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT * FROM station";

        return jdbcTemplate.query(sql, STATION_ROW_MAPPER);
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM station WHERE id = ?";

        return jdbcTemplate.update(sql, id);
    }
}
