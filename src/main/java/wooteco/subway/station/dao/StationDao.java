package wooteco.subway.station.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.exception.StationException;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;
    private final KeyHolder keyHolder = new GeneratedKeyHolder();

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(final String name) {
        final String sql = "INSERT INTO STATION (name) VALUES (?)";
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, name);
            return ps;
        }, keyHolder);

        return keyHolder.getKeyAs(Long.class);
    }

    public void delete(final Long id) {
        final String sql = "DELETE FROM STATION WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Station findById(final Long id) {
        try {
            final String sql = "SELECT * FROM STATION WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new StationException("존재하지 않는 역입니다.");
        }
    }

    public List<Station> findAll() {
        final String sql = "SELECT * FROM STATION";
        return jdbcTemplate.query(sql, rowMapper());
    }

    public boolean isNotExist(final Long id) {
        final String sql = "SELECT EXISTS(SELECT * FROM STATION WHERE id = ?)";
        return !jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    public boolean isExistingName(final String name) {
        final String sql = "SELECT EXISTS(SELECT * FROM STATION WHERE name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }

    private RowMapper<Station> rowMapper() {
        return (rs, rn) -> new Station(
                rs.getLong("id"),
                rs.getString("name")
        );
    }
}
