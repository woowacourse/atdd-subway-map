package wooteco.subway.line;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class LineDao {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static void delete(Long id) {
        lines.stream()
            .filter(line -> line.getId().equals(id))
            .findFirst()
            .ifPresent(line -> lines.remove(line));
    }

    public List<Line> findAll() {
        String query = "SELECT * FROM line";
        List<Line> lines = jdbcTemplate.query(query, (resultSet, rowNum) -> {
            Line line = new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color")
            );
            return line;
        });
        return lines;
    }

    public Line find(Long id) {
        String query = "SELECT * FROM line WHERE id = ?";
        return jdbcTemplate.queryForObject(query,
            (resultSet, rowNum) -> {
                Line line = new Line(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color")
                );
                return line;
            }, id);
    }

    public void modify(Long id, LineRequest lineRequest) {
        String query = "UPDATE line SET name=(?), color=(?) WHERE id = (?)";
        jdbcTemplate.update(query, lineRequest.getName(), lineRequest.getColor(), id);
    }

    public long save(Line line) {
        String query = "INSERT INTO line(name, color) VALUES(?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }
}
