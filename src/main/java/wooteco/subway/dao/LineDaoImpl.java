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
import wooteco.subway.exception.datanotfound.DataNotFoundException;
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
        final String sql = "insert into Line (name, color) values (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);
        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    @Override
    public List<Line> findAll() {
        final String sql = "select * from line";
        return jdbcTemplate.query(sql, lineRowMapper());
    }

    @Override
    public Line findById(Long id) {
        try {
            final String sql = "select * from line where id = (?)";
            return jdbcTemplate.queryForObject(sql, lineRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new LineNotFoundException("존재하지 않는 노선입니다.");
        }
    }

    @Override
    public int update(Long id, Line line) {
        final String sql = "update line set (name, color) = (?, ?) where id = ?";
        return jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
    }

    @Override
    public int delete(Long id) {
        final String sql = "delete from line where id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
