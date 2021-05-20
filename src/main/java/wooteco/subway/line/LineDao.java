package wooteco.subway.line;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.NoSuchDataException;

@Repository
public class LineDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void delete(Long id) {
        String query = "DELETE FROM line WHERE id = ?";
        int affectedRowNumber = jdbcTemplate.update(query, id);

        if (affectedRowNumber == 0) {
            throw new NoSuchDataException("없는 노선입니다.");
        }
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

    public Line findById(Long id) {
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

    public void update(Long id, LineRequest lineRequest) {
        String query = "UPDATE line SET name=(?), color=(?) WHERE id = (?)";
        int affectedRowNumber = jdbcTemplate
            .update(query, lineRequest.getName(), lineRequest.getColor(), id);
        if (affectedRowNumber == 0) {
            throw new NoSuchDataException("존재하지 않아 변경할 수 없습니다.");
        }
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
