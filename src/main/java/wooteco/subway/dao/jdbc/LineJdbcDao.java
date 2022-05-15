package wooteco.subway.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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

    private static final RowMapper<Line> LINE_ROW_MAPPER = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color")
    );

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
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO LINE (name, color) VALUES (?, ?)", new String[]{"id"});
            preparedStatement.setString(1, line.getName());
            preparedStatement.setString(2, line.getColor());
            return preparedStatement;
        }, keyHolder);
        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
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
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, LINE_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public void update(final Line line) throws NoSuchLineException, IllegalArgumentException {
        if (ObjectUtils.isEmpty(line)) {
            throw new IllegalArgumentException("passed line is null");
        }

        final String sql = "UPDATE LINE SET name = (?), color = (?) WHERE id = (?)";
        try {
            int affectedRow = jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
            checkUpdated(affectedRow, line.getId());
        } catch (DuplicateKeyException exception) {
            throw new DuplicateLineException();
        }
    }

    @Override
    public void deleteById(final Long id) {
        final String sql = "DELETE FROM LINE WHERE id = (?)";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existByName(final String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE name = (?))";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, name));
    }

    @Override
    public boolean existByColor(final String color) {
        final String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE color = (?))";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, color));
    }

    private void checkUpdated(final int affectedRow, final Long id) {
        if (affectedRow == 0) {
            throw new NoSuchLineException(id);
        }
    }
}
