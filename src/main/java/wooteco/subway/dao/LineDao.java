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

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {

        String sql = "insert into LINE (name, color) values (?, ?)";
        jdbcTemplate.update(sql, line.getName(), line.getColor());

        return createNewObject(line);
    }

    public void checkDuplication(Line line) {
        String sql = String.format("select count(*) from LINE where name = '%s'", line.getName());

        if (jdbcTemplate.queryForObject(sql, Integer.class) > 0) {
            throw new IllegalArgumentException("이미 존재하는 노선 이름입니다.");
        }
    }

    public List<Line> findAll() {
        String sql = "select * from LINE";
        return jdbcTemplate.query(sql, new LineMapper());
    }

    public void delete(Long id) {
        String sql = "delete from LINE where id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class LineMapper implements RowMapper<Line> {
        public Line mapRow(ResultSet rs, int rowCnt) throws SQLException {
            return new Line(rs.getLong("id"), rs.getString("name"), rs.getString("color"));
        }
    }

    private Line createNewObject(Line line) {
        String sql = "select max(id) from LINE";
        Long id = jdbcTemplate.queryForObject(sql, Long.class);
        return new Line(id, line.getName(), line.getColor());
    }

    public Line findById(Long id) {
        String sql = String.format("select * from LINE where id = %d", id);
        return jdbcTemplate.queryForObject(sql, new LineMapper());
    }

    public void edit(Long id, String name, String color) {
        String sql = "update LINE set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, name, color, id);
    }
}
