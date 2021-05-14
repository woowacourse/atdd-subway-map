package wooteco.subway.station.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.Stations;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class StationRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    public StationRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(final Station station) {
        String query = "INSERT INTO STATION(name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public Stations findAll() {
        String query = "SELECT id, name FROM station ORDER BY id";
        List<Station> stations = jdbcTemplate.query(query, stationRowMapper);
        return new Stations(stations);
    }

    public void deleteById(final Long id) {
        String query = "DELETE FROM station WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    public String findNameById(final Long id) {
        String query = "SELECT name FROM station WHERE id = ?";
        return jdbcTemplate.queryForObject(query, String.class, id);
    }

    public boolean doesNameExist(final Station station) {
        String query = "SELECT EXISTS(SELECT * FROM station WHERE name = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, station.getName());
    }

    public boolean doesIdNotExist(final Long id) {
        String query = "SELECT NOT EXISTS(SELECT * FROM station WHERE id = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, id);
    }
}
