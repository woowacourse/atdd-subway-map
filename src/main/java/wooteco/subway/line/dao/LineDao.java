package wooteco.subway.line.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.FinalStations;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.exception.LineException;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final KeyHolder keyHolder = new GeneratedKeyHolder();

    public LineDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(final Line line) {
        final String sql = "INSERT INTO LINE (name, color, first_station_id, last_station_id) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(con -> {
            final PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            ps.setLong(3, line.getFirstStationId());
            ps.setLong(4, line.getLastStationId());
            return ps;
        }, keyHolder);

        return keyHolder.getKeyAs(Long.class);
    }

    public void delete(final Long id) {
        final String sql = "DELETE FROM LINE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void update(final Line line) {
        final String sql = "UPDATE LINE SET NAME = ?, COLOR =? WHERE id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
    }

    public void updateFinalStations(final Long id, final FinalStations finalStations) {
        final String sql = "UPDATE LINE SET first_station_id = ?, last_station_id =? WHERE id = ?";
        jdbcTemplate.update(sql, finalStations.firstStationId(), finalStations.lastStationId(), id);
    }

    public Long findFirstStationId(final Long id) {
        final String sql = "SELECT first_station_id FROM LINE WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, id);
    }

    public Line findById(final Long id) {
        try {
            final String sql = "SELECT * FROM LINE WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new LineException("존재하지 않는 노선입니다.");
        }
    }

    public boolean isNotExist(final Long id) {
        final String sql = "SELECT EXISTS(SELECT from LINE WHERE id = ?)";
        return !jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    public boolean isExistingName(final String name) {
        final String sql = "SELECT EXISTS(SELECT from LINE WHERE name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }

    public List<Line> findAll() {
        final String sql = "SELECT * FROM LINE";
        return jdbcTemplate.query(sql, rowMapper());
    }

    public FinalStations finalStations(final Long id) {
        final String sql = "SELECT first_station_id, last_station_id FROM LINE WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new FinalStations(
                rs.getLong("first_station_id"),
                rs.getLong("last_station_id")
        ), id);
    }

    private RowMapper<Line> rowMapper() {
        return (rs, rowNum) -> new Line(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("color"),
                rs.getLong("first_station_id"),
                rs.getLong("last_station_id")
        );
    }
}
