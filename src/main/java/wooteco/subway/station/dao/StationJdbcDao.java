package wooteco.subway.station.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class StationJdbcDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Station save(Station station) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO STATION (name) VALUES (?)";
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Station(id, station.getName());
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT id, name FROM STATION";
        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> new Station(
                rs.getLong("id"),
                rs.getString("name")
            )
        );
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM STATION WHERE id=?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Station> findById(Long id) {
        String sql = "SELECT name FROM STATION WHERE id=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> new Station(
                    id,
                    rs.getString("name")
                ),
                id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Station> findByName(String name) {
        String sql = "SELECT id FROM STATION WHERE name=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> new Station(
                    rs.getLong("id"),
                    name
                ),
                name));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean doesNotExist(Long id) {
        String sql = "SELECT NOT EXISTS (SELECT * FROM STATION WHERE id=?) AS noExist";
        return jdbcTemplate.queryForObject(sql, Boolean.TYPE, id);
    }
}
