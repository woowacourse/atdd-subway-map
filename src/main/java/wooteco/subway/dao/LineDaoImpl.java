package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.datanotfound.LineNotFoundException;

@Repository
public class LineDaoImpl implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Line> lineRowMapper() {
        return (rs, rowNum) ->
                new Line(rs.getLong("id"),
                        rs.getString("name"), rs.getNString("color"));
    }

    @Override
    public Line save(Line line) {
        final String sql = "INSERT INTO Line (name, color, deleted) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            ps.setBoolean(3, false);
            return ps;
        }, keyHolder);
        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    @Override
    public List<Line> findAll() {
        final String sql = "SELECT * FROM line WHERE deleted = (?)";
        return jdbcTemplate.query(sql, lineRowMapper(), false);
    }

    @Override
    public Line findById(Long id) {
        try {
            final String sql = "SELECT * FROM line WHERE id = (?) AND deleted = (?)";
            return jdbcTemplate.queryForObject(sql, lineRowMapper(), id, false);
        } catch (EmptyResultDataAccessException e) {
            throw new LineNotFoundException("존재하지 않는 노선입니다.");
        }
    }

    @Override
    public int update(Line line) {
        final String sql = "UPDATE line SET (name, color) = (?, ?) WHERE id = ? AND deleted = (?)";
        return jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId(), false);
    }

    @Override
    public int delete(Long id) {
        final String sql = "UPDATE line SET deleted = (?) WHERE id = (?)";
        return jdbcTemplate.update(sql, true, id);
    }
}
