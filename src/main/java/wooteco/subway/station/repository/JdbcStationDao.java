package wooteco.subway.station.repository;

import org.springframework.dao.support.DataAccessUtils;
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
public class JdbcStationDao implements StationDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationRowMapper = (rs, rowNum) -> new Station(
            rs.getLong("id"),
            rs.getString("name")
    );

    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
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

    @Override
    public List<Station> findAll() {
        String query = "SELECT * FROM station";
        return jdbcTemplate.query(query, stationRowMapper);
    }

    @Override
    public boolean findByName(String name) {
        String query = "SELECT COUNT(*) FROM station WHERE name = (?)";
        return jdbcTemplate.queryForObject(query, Integer.class, name) > 0;
    }

    @Override
    public void deleteById(Long id) {
        String query = "DELETE FROM station WHERE id = (?)";
        jdbcTemplate.update(query, id);
    }

    @Override
    public Station findBy(Long id) {
        String query = "SELECT * FROM station WHERE id = ?";
        List<Station> stations = jdbcTemplate.query(query, stationRowMapper, id);
        return DataAccessUtils.singleResult(stations);
    }

    @Override
    public boolean isExistingStation(Long stationId) {
        String query = "SELECT EXISTS (SELECT * FROM station WHERE id = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, stationId);
    }
}
