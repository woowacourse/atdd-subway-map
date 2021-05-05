package wooteco.subway.line;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.DuplicationException;
import wooteco.subway.exception.NotFoundException;

@Repository
public class LineDao {

    private static Long seq = 0L;
    private static final List<Line> lines = new ArrayList<>();

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        validateDuplicateNameAndColor(line);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO line (`name`, color) VALUES (?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                .prepareStatement(sql, new String[]{"id", "name", "color"});
            preparedStatement.setString(1, line.getName());
            preparedStatement.setString(2, line.getColor());
            return preparedStatement;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        return new Line((Long) keys.get("id"), (String) keys.get("name"),
            (String) keys.get("color"));
    }

    private void validateDuplicateNameAndColor(Line line) {
        if (isDuplicateName(line)) {
            throw new DuplicationException("이미 존재하는 노선 이름입니다.");
        }

        if (isDuplicateColor(line)) {
            throw new DuplicationException("이미 존재하는 노선 색깔입니다.");
        }
    }

    private boolean isDuplicateColor(Line newLine) {
        String sql = "SELECT id FROM line WHERE color = ?";
        try {
            jdbcTemplate.queryForObject(sql, Long.class, newLine.getColor());
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private boolean isDuplicateName(Line newLine) {
        String sql = "SELECT id FROM line WHERE name = ?";
        try {
            jdbcTemplate.queryForObject(sql, Long.class, newLine.getName());
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public List<Line> findAll() {
        String sql = "SELECT id, name, color FROM line";
        return jdbcTemplate.query(sql, (rs, rowNum) -> getLine(rs));
    }

    private Line getLine(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String color = rs.getString("color");
        return new Line(id, name, color);
    }

    public static Line findLineById(Long id) {
        return lines.stream()
            .filter(line -> line.isSameId(id))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("존재하지 않는 노선 ID 입니다."));
    }

//    public static void update(Line updatedLine) {
//        validateDuplicateNameAndColor(updatedLine);
//
//        Integer index = lines.stream()
//            .filter(line -> line.isSameId(updatedLine.getId()))
//            .map(line -> lines.indexOf(line))
//            .findAny()
//            .orElseThrow(() -> new NotFoundException("존재하지 않는 노선 ID 입니다."));
//        lines.set(index, updatedLine);
//    }

    public static void deleteLineById(Long id) {
        Integer index = lines.stream()
            .filter(line -> line.isSameId(id))
            .map(line -> lines.indexOf(line))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
        lines.remove(index);
    }

    public static void deleteAll() {
        lines.clear();
    }
}
