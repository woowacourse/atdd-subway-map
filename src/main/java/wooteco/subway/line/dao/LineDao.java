package wooteco.subway.line.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.DuplicationException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.line.model.Line;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Line> mapperLine = (rs, rowNum) -> {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String color = rs.getString("color");
        return new Line(id, name, color);
    };

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        validateDuplicateNameAndColor(line);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO line (`name`, color) VALUES (?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, line.getName());
            preparedStatement.setString(2, line.getColor());
            return preparedStatement;
        }, keyHolder);

        long id = keyHolder.getKey().longValue();
        return findLineById(id);
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
        return jdbcTemplate.query(sql, mapperLine);
    }

    public Line findLineById(Long id) {
        String sql = "SELECT id, name, color FROM line WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapperLine, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("존재하지 않는 노선 ID 입니다.");
        }
    }

    public void update(Line updatedLine) {
        validateDuplicateNameAndColor(updatedLine);
        String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        int updateCount = jdbcTemplate
            .update(sql, updatedLine.getName(), updatedLine.getColor(), updatedLine.getId());
        if (updateCount == 0) {
            throw new NotFoundException("존재하지 않는 노선 ID 입니다.");
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM line WHERE id = ?";
        int updateCount = jdbcTemplate.update(sql, id);
        if (updateCount == 0) {
            throw new NotFoundException("존재하지 않는 노선 ID 입니다.");
        }
    }
}
