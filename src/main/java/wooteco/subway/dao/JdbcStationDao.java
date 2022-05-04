package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class JdbcStationDao {

    private final JdbcTemplate jdbcTemplate;

    private RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Station station) {
        final String sql = "insert into STATION (name) values (?)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public Station findById(Long id) {
        final String sql = "select * from STATION where id = ?";
        return jdbcTemplate.queryForObject(sql, stationRowMapper, id);
    }

    public List<Station> findAll() {
        final String sql = "select * from STATION";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public boolean hasStation(String name) {
        final String sql = "select exists (select * from STATION where name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }

    public void updateById(Long id, String name) {
        final String sql = "update STATION set name = ? where id = ?";
        jdbcTemplate.update(sql, name, id);
    }

    public void deleteById(Long id) {
        final String sql = "delete from STATION where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
