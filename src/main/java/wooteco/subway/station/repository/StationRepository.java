package wooteco.subway.station.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.domain.Station;

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

    public boolean isExistName(final Station station) {
        String query = "SELECT EXISTS(SELECT * FROM STATION WHERE name = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, station.getName());
    }

    public Station save(final Station station) {
        String query = "INSERT INTO STATION(name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        return new Station(
                Objects.requireNonNull(keyHolder.getKey()).longValue(),
                station.getName()
        );
    }

    public List<Station> getStations() {
        String query = "SELECT id, name FROM station ORDER BY id";
        return jdbcTemplate.query(query, stationRowMapper);
    }

    public void deleteById(final Long id) {
        try {
            String query = "DELETE FROM station WHERE id = ?";
            jdbcTemplate.update(query, id);
        } catch (Exception e) {
            throw new IllegalArgumentException("존재하지 않는 id 입니다");
        }
    }
}
