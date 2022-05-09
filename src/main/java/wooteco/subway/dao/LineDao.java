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

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.lineMapper = new LineMapper();
    }

    public Line save(Line line) {
        String sql = "insert into Line (name, color, upStationId, downStationId, distance) values (?, ?, ?, ?, ?)";
        Section section = line.getSection();
        jdbcTemplate.update(sql, line.getName(), line.getColor(), section.getUpStationId(),
                section.getDownStationId(), section.getDistance());
        return includeIdIn(line);
    }

    public int counts(String name) {
        String sql = String.format("select count(*) from Line where name = '%s'", name);
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public List<Line> findAll() {
        String sql = "select * from Line";
        return jdbcTemplate.query(sql, lineMapper);
    }

    private Line includeIdIn(Line line) {
        String sql = "select max(id) from Line";
        Long id = jdbcTemplate.queryForObject(sql, Long.class);
        return new Line(id, line.getName(), line.getColor(),
                line.getSection());
    }

    public Line findById(Long id) {
        String sql = String.format("select * from Line where id = %d", id);
        return jdbcTemplate.queryForObject(sql, lineMapper);
    }

    public void edit(Long id, String name, String color) {
        String sql = "update Line set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, name, color, id);
    }

    public void deleteById(Long id) {
        String sql = "delete from Line where id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class LineMapper implements RowMapper<Line> {
        public Line mapRow(ResultSet rs, int rowCnt) throws SQLException {
            return new Line(rs.getLong("id"), rs.getString("name"), rs.getString("color"),
                    new Section(rs.getLong("upStationId"), rs.getLong("downStationId"), rs.getInt("distance")));
        }
    }
}
