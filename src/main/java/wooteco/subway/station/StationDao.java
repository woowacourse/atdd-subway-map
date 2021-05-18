package wooteco.subway.station;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;

    private RowMapper<Station> stationRowMapper() {
        return (resultSet, rowNum) -> new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
    }

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(String name) {
        String sql = "insert into STATION (name) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, name);
            return ps;
        }, keyHolder);
        return new Station(Objects.requireNonNull(keyHolder.getKey())
                                  .longValue(), name);
    }

    public List<Station> findAll() {
        String sql = "select id, name from STATION";
        return jdbcTemplate.query(sql, stationRowMapper());
    }

    public Optional<Station> findByName(String name) throws IncorrectResultSizeDataAccessException {
        String sql = "select id, name from STATION where name = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, stationRowMapper(), name));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Station> findById(Long id) {
        String sql = "select id, name from STATION where id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, stationRowMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void delete(Long id) {
        String sql = "delete from STATION where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
