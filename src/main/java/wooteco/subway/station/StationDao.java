package wooteco.subway.station;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Station> stationRowMapper() {
        return (resultSet, rowNum) -> new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
    }

    public Station save(String name) {
        String sql = "insert into STATION (name) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, name);
            return ps;
        }, keyHolder);
        return new Station(keyHolder.getKey()
                .longValue(), name);
    }

    public List<Station> findAll() {
        String sql = "select id, name from STATION";
        return jdbcTemplate.query(sql, stationRowMapper());
    }

    public Optional<Station> findByName(String name) {
        String sql = "select id from STATION where name = ?";

        try {
            Long findStationId = Long.valueOf(jdbcTemplate.queryForObject(sql, String.class, name));
            return Optional.ofNullable(new Station(findStationId, name));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int delete(Long id) {
        String sql = "delete from STATION where id = ? and not exists (" +
                "select * from SECTION where up_station_id = ? or down_station_id = ?)";
        return jdbcTemplate.update(sql, id, id, id);
    }

    public Optional<Station> findById(Long id) {
        String sql = "select name from STATION where id = ?";
        String name = jdbcTemplate.queryForObject(sql, String.class, id);

        try {
            return Optional.ofNullable(new Station(id, name));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
