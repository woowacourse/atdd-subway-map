package wooteco.subway.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.line.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.exception.ExceptionInformation;
import wooteco.subway.exception.SubwayException;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class LineDao {
    private JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Line line) {
        String query = "INSERT INTO line(name, color) VALUES(?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        executeSaveQuery(line, query, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private void executeSaveQuery(Line line, String query, KeyHolder keyHolder) {
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
                ps.setString(1, line.getName());
                ps.setString(2, line.getColor());
                return ps;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new SubwayException(ExceptionInformation.DUPLICATE_LINE_NAME_WHEN_INSERT);
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

    public Line find(Long id) {
        String query = "SELECT * FROM line WHERE id = ?";
        try {
            return selectLine(id, query);
        } catch (EmptyResultDataAccessException e) {
            throw new SubwayException(ExceptionInformation.LINE_NOT_FOUND_WHEN_LOOKUP);
        }
    }

    private Line selectLine(Long id, String query) {
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

    public void modify(Long id, Line line) {
        String query = "UPDATE line SET name=(?), color=(?) WHERE id = (?)";
        int affectedRowNumber = 0;

        affectedRowNumber = executeUpdateQuery(id, line, query);

        if (affectedRowNumber == 0) {
            throw new SubwayException(ExceptionInformation.LINE_NOT_FOUND_WHEN_MODIFY);
        }
    }

    private int executeUpdateQuery(Long id, Line line, String query) {
        int affectedRowNumber;
        try {
            affectedRowNumber = jdbcTemplate.update(query, line.getName(), line.getColor(), id);
        } catch (DuplicateKeyException e) {
            throw new SubwayException(ExceptionInformation.DUPLICATE_LINE_NAME_WHEN_MODIFY);
        }
        return affectedRowNumber;
    }

    public void delete(Long id) {
        String query = "DELETE FROM line WHERE id = ?";
        int affectedRowNumber = jdbcTemplate.update(query, id);

        if (affectedRowNumber == 0) {
            throw new SubwayException(ExceptionInformation.LINE_NOT_FOUND_WHEN_DELETE);
        }
    }
}
