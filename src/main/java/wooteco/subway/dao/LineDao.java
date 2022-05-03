package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

@Repository
public class LineDao {
    public static final String DUPLICATE_LINE_NAME = "[ERROR] 중복된 노선 이름이 있습니다.";
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> new Line(resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color"));

    public Line save(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into line (name, color) values (?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);
        long insertedId = keyHolder.getKey().longValue();

        return new Line(insertedId, line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        String sql = "select id, name, color from line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public void deleteById(Long id) {
        String sql = "delete from line where id = (?)";
        this.jdbcTemplate.update(sql,id);
    }

    public Line findById(Long id) {
        String sql = "select id, name, color from line where id = (?)";
        return jdbcTemplate.queryForObject(sql, lineRowMapper, id);
    }

    public void changeLineName(Long id, String newName) {
        String sql = "update line set name = (?) where id = (?)";
        jdbcTemplate.update(sql, newName, id);
    }
}
