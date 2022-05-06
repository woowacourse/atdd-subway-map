package wooteco.subway.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DuplicateLineException;
import wooteco.subway.exception.NoSuchLineException;

@Repository
public class LineJdbcDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineJdbcDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(final Line line) {
        final String sql = "INSERT INTO LINE (name, color) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
                preparedStatement.setString(1, line.getName());
                preparedStatement.setString(2, line.getColor());
                return preparedStatement;
            }, keyHolder);
        } catch (DuplicateKeyException exception) {
            throw new DuplicateLineException();
        }

        return keyHolder.getKey().longValue();
    }

    @Override
    public List<Line> findAll() {
        final String sql = "SELECT id, name, color FROM LINE";

        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color")
        ));
    }

    @Override
    public Line findById(final Long id) {
        final String sql = "SELECT id, name, color FROM LINE WHERE id = (?)";

        try {
            return jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> new Line(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color")
            ), id);
        } catch (EmptyResultDataAccessException exception) {
            throw new NoSuchLineException();
        }
    }

    @Override
    public Long update(final Long id, final String name, final String color) {
        final String sql = "UPDATE LINE SET name = (?), color = (?) WHERE id = (?)";

        try {
            int affectedRow = jdbcTemplate.update(sql, name, color, id);
            if (isNoUpdateOccurred(affectedRow)) {
                throw new NoSuchLineException();
            }
        } catch (DuplicateKeyException exception) {
            throw new DuplicateLineException();
        }
        return id;
    }

    private boolean isNoUpdateOccurred(final int affectedRow) {
        return affectedRow == 0;
    }

    @Override
    public void deleteById(final Long id) {
        final String sql = "DELETE FROM LINE WHERE id = (?)";

        jdbcTemplate.update(sql, id);
    }
}
