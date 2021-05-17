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
        final String sql = "INSERT INTO LINE (name, color, up_station_id, down_station_id) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(con -> {
            final PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            ps.setLong(3, line.getUpStationId());
            ps.setLong(4, line.getDownStationId());
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
        updateFinalStations(id, finalStations.upStationId(), finalStations.downStationId());
    }

    public void updateFinalStations(final Long id, final Long upStationId, final Long downStationId) {
        final String sql = "UPDATE LINE SET up_station_id = ?, down_station_id =? WHERE id = ?";
        jdbcTemplate.update(sql, upStationId, downStationId, id);
    }

    public Long findUpStationId(final Long id) {
        final String sql = "SELECT up_station_id FROM LINE WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, id);
    }

    public Long findDownStationId(final Long id) {
        final String sql = "SELECT down_station_id FROM LINE WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, id);
    }

    public boolean isUpStation(final Long id, final Long upStationId) {
        return findUpStationId(id).equals(upStationId);
    }

    public boolean isDownStation(final Long id, final Long downStationId) {
        return findDownStationId(id).equals(downStationId);
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

    private RowMapper<Line> rowMapper() {
        return (rs, rowNum) -> new Line(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("color"),
                rs.getLong("up_station_id"),
                rs.getLong("down_station_id")
        );
    }

    public FinalStations finalStations(final Long id) {
        final String sql = "SELECT up_station_id, down_station_id FROM LINE WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum)-> new FinalStations(
                rs.getLong("up_station_id"),
                rs.getLong("down_station_id")
        ), id);
    }
}
