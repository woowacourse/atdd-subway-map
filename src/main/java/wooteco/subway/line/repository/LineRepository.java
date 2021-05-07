package wooteco.subway.line.repository;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.line.domain.Line;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class LineRepository {
    private static final int NO_EXIST_COUNT = 0;
    private final JdbcTemplate jdbcTemplate;

    public LineRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("color"),
            resultSet.getString("name")
    );

    public boolean isExistName(final Line line) {
        String query = "SELECT EXISTS(SELECT * FROM Line WHERE name = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, line.getName());
    }

    public Line save(final Line line) {
        try {
            String query = "INSERT INTO line(color, name) VALUES(?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(Connection -> {
                PreparedStatement ps = Connection.prepareStatement(query, new String[]{"id"});
                ps.setString(1, line.getColor());
                ps.setString(2, line.getName());
                return ps;
            }, keyHolder);
            return new Line(Objects.requireNonNull(keyHolder.getKey()).longValue(), line.getColor(), line.getName());
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("중복되는 LineName 입니다.");
        }
    }

    public List<Line> getLines() {
        String query = "SELECT id, color, name FROM line ORDER BY id";
        return jdbcTemplate.query(query, lineRowMapper);
    }

    public Line getLineById(final Long id) {
        try {
            String query = "SELECT id, color, name FROM line WHERE id = ?";
            return jdbcTemplate.queryForObject(
                    query,
                    (resultSet, rowNum) -> new Line(
                            resultSet.getLong("id"),
                            resultSet.getString("color"),
                            resultSet.getString("name")
                    ), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("존재하지 않는 id 입니다.");
        }
    }

    public void update(final Line line) {
        String query = "UPDATE line SET color = ?, name = ? WHERE id = ?";
        int affectedRowCount = jdbcTemplate.update(query, line.getColor(), line.getName(), line.getId());
        if (affectedRowCount == NO_EXIST_COUNT) {
            throw new NotFoundException("존재하지 않는 Line입니다.");
        }
    }

    public void deleteById(final Long id) {
        String query = "DELETE FROM line WHERE id = ?";
        int affectedRowCount = jdbcTemplate.update(query, id);
        if (affectedRowCount == NO_EXIST_COUNT) {
            throw new NotFoundException("존재하지 않는 id 입니다.");
        }
    }
}
