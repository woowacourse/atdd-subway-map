package wooteco.subway.dao;

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
import wooteco.subway.domain.Station;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into station (name) values (?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        long savedId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return new Station(savedId, station.getName());
    }

    public Optional<Station> findById(Long id) {
        String sql = "select * from station where id = ?";
        try {
            Station station = jdbcTemplate.queryForObject(sql, rowMapper(), id);
            return Optional.ofNullable(station);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public Optional<Station> findByName(String name) {
        String sql = "select * from station where name = ?";
        try {
            Station station = jdbcTemplate.queryForObject(sql, rowMapper(), name);
            return Optional.ofNullable(station);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public List<Station> findAll() {
        String sql = "select * from station";
        return jdbcTemplate.query(sql, rowMapper());
    }

    public void delete(Station station) {
        String sql = "delete from station where id = ?";
        jdbcTemplate.update(sql, station.getId());
    }

    private RowMapper<Station> rowMapper() {
        return (rs, rowNum) ->
            new Station(
                rs.getLong("id"),
                rs.getString("name"));
    }
}
