package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class LineDao {
    private static final String NO_ID_LINE_ERROR_MESSAGE = "해당 아이디의 노선이 없습니다.";
    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Line line) {
        final String sql = "INSERT INTO line (name, color) VALUES (?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, line.getName());
            preparedStatement.setString(2, line.getColor());
            return preparedStatement;
        }, keyHolder);
    }

    public Line find(String name) {
        final String sql = "SELECT * FROM line WHERE name = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Line(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("color")),
                    name);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException(NO_ID_LINE_ERROR_MESSAGE);
        }
    }

    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Line(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("color")
        ));
    }

    public void update(Long id, Line line) {
        final String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
    }

    public void delete(Long id) {
        final String sql = "delete from line where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
