package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.BadRequestException;

@Repository
public class LineDao {
    private static final String LINE_NAME_OR_COLOR_DUPLICATE_ERROR_MESSAGE = "노선의 이름 또는 색깔이 이미 존재합니다.";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) ->
        new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color")
        );

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Line line) {
        String sql = "INSERT INTO LINE (name, color) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            saveNewLine(line, sql, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new BadRequestException(LINE_NAME_OR_COLOR_DUPLICATE_ERROR_MESSAGE);
        }
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private void saveNewLine(Line line, String sql, KeyHolder keyHolder) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);
    }

    public Optional<Line> findById(Long id) {
        String query = "SELECT * FROM LINE WHERE id = ?";
        Line result = DataAccessUtils.singleResult(
            jdbcTemplate.query(query, lineRowMapper, id)
        );
        return Optional.ofNullable(result);
    }

    public List<Line> findAll() {
        String query = "SELECT * FROM LINE";
        return jdbcTemplate.query(query, lineRowMapper);
    }

    public int update(Long id, String color, String name) {
        String query = "UPDATE LINE SET color = ?, name = ? WHERE id = ?";
        try {
            return jdbcTemplate.update(query, color, name, id);
        } catch (DuplicateKeyException e) {
            throw new BadRequestException(LINE_NAME_OR_COLOR_DUPLICATE_ERROR_MESSAGE);
        }
    }

    public int deleteById(Long id) {
        String query = "DELETE FROM LINE WHERE id = ?";
        return jdbcTemplate.update(query, id);
    }
}
