package wooteco.subway.station.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class JdbcStationDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationRowMapper = (rs, rowNum) -> new Station(
            rs.getLong("id"),
            rs.getString("name")
    );

    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO station (name) VALUES (?)";
        String name = station.getName();
        jdbcTemplate.update(con -> {
            PreparedStatement pstmt = con.prepareStatement(query, new String[]{"id"});
            pstmt.setString(1, name);
            return pstmt;
        }, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Station(id, name);
    }

    public List<Station> findAll() {
        String query = "SELECT * FROM station";
        return jdbcTemplate.query(query, stationRowMapper);
    }

    public boolean findByName(String name) {
        String query = "SELECT COUNT(*) FROM station WHERE name = (?)";
        return jdbcTemplate.queryForObject(query, Integer.class, name) > 0;
    }

    public List<Station> findTwoStations(Long upStationId, Long downStationId) {
        String query = "SELECT * FROM station WHERE id in (?, ?)";
        return jdbcTemplate.query(query, stationRowMapper, upStationId, downStationId);
    }

    public void deleteById(Long id) {
        String query = "DELETE FROM station WHERE id = (?)";
        jdbcTemplate.update(query, id);
    }

    public Station findBy(Long id) {
        String query = "SELECT * FROM station WHERE id = ?";
        return jdbcTemplate.queryForObject(query, stationRowMapper, id);
    }
}
