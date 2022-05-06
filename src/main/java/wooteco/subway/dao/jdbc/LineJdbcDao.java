package wooteco.subway.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
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
    public Line save(final Line line) throws IllegalArgumentException {
        if (ObjectUtils.isEmpty(line)) {
            throw new IllegalArgumentException("passed line is null");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            trySaveLine(line, keyHolder);
        } catch (DuplicateKeyException exception) {
            throw new DuplicateLineException();
        }
        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    private void trySaveLine(final Line line, final KeyHolder keyHolder)
            throws DuplicateKeyException {
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO LINE (name, color) VALUES (?, ?)", new String[]{"id"});
            preparedStatement.setString(1, line.getName());
            preparedStatement.setString(2, line.getColor());
            return preparedStatement;
        }, keyHolder);
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
    public Optional<Line> findById(final Long id) {
        final String sql = "SELECT id, name, color FROM LINE WHERE id = (?)";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> new Line(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color")
            ), id));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public void update(final Long id, final String name, final String color)
            throws NoSuchLineException, DuplicateLineException {

        final String sql = "UPDATE LINE SET name = (?), color = (?) WHERE id = (?)";
        try {
            int affectedRow = jdbcTemplate.update(sql, name, color, id);
            checkUpdated(affectedRow);
        } catch (DuplicateKeyException exception) {
            throw new DuplicateLineException();
        }
    }

    private void checkUpdated(final int affectedRow) {
        if (affectedRow == 0) {
            throw new NoSuchLineException();
        }
    }

    @Override
    public void deleteById(final Long id) {
        final String sql = "DELETE FROM LINE WHERE id = (?)";
        jdbcTemplate.update(sql, id);
    }
}
