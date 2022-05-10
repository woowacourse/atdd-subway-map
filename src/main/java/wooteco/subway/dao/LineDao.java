package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;

    private final LineMapper lineMapper;

    public LineDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.lineMapper = new LineMapper();
    }

    public Line save(final Line line) {
        final String sql = "insert into Line (name, color) values (?, ?)";
        jdbcTemplate.update(sql, line.getName(), line.getColor());
        return includeIdIn(line);
    }

    public int counts(final String name) {
        final String sql = String.format("select count(*) from Line where name = '%s'", name);
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public List<Line> findAll() {
        final String sql = "select * from Line";
        return jdbcTemplate.query(sql, lineMapper);
    }

    private Line includeIdIn(final Line line) {
        final String sql = "select max(id) from Line";
        final Long id = jdbcTemplate.queryForObject(sql, Long.class);
        return new Line(id, line.getName(), line.getColor());
    }

    public Line findById(final Long id) {
        final String sql = String.format("select * from Line where id = %d", id);
        return jdbcTemplate.queryForObject(sql, lineMapper);
    }

    public void edit(final Long id, final String name, final String color) {
        final String sql = "update Line set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, name, color, id);
    }

    public void deleteById(final Long id) {
        final String sql = "delete from Line where id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class LineMapper implements RowMapper<Line> {
        public Line mapRow(final ResultSet rs, final int rowCnt) throws SQLException {
            return new Line(rs.getLong("id"), rs.getString("name"), rs.getString("color"));
        }
    }
}
