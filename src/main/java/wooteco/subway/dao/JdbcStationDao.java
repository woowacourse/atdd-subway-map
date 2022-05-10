package wooteco.subway.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Station;

@Repository
public class JdbcStationDao implements StationDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> new Station(
        resultSet.getLong("id"),
        resultSet.getString("name")
    );

    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO STATION(name) VALUES(?)";

        jdbcTemplate.update((Connection conn) -> {
            PreparedStatement pstmt = conn.prepareStatement(sql, new String[] {"id"});
            pstmt.setString(1, station.getName());
            return pstmt;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        return getStation(id);
    }

    public Station getStation(Long id) {
        String sql = "SELECT * FROM STATION WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, stationRowMapper, id);
    }

    public boolean existByName(String name) {
        String sql = "SELECT EXISTS(SELECT id FROM STATION WHERE name = ?) AS SUCCESS";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }

    public List<Station> findAll() {
        String sql = "SELECT id, name FROM STATION";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM STATION WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public boolean existById(Long id) {
        String sql = "SELECT EXISTS(SELECT * FROM STATION WHERE id = ?) AS SUCCESS";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }
}
