package wooteco.subway.dao;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.line.Line;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class LineDao {
    private static final RowMapper<Line> ROW_MAPPER = (resultSet, rowNumber) -> {
        long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String color = resultSet.getString("color");
        return new Line(id, name, color);
    };
    private static final int ROW_COUNTS_FOR_ID_NOT_FOUND = 0;

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Line line) {
        String query = "INSERT INTO LINE (name, color) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = getPreparedStatementCreator(line, query);
        try {
            jdbcTemplate.update(preparedStatementCreator, keyHolder);
            return keyHolder.getKey().longValue();
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new SubwayException(ExceptionStatus.DUPLICATED_NAME);
        }
    }

    private PreparedStatementCreator getPreparedStatementCreator(Line line, String query) {
        return (connection) -> {
            PreparedStatement prepareStatement = connection.prepareStatement(query, new String[]{"id"});
            prepareStatement.setString(1, line.getName());
            prepareStatement.setString(2, line.getColor());
            return prepareStatement;
        };
    }

    public List<Line> findAll() {
        String query = "SELECT ID, NAME, COLOR FROM LINE";
        return jdbcTemplate.query(query, ROW_MAPPER);
    }

    public Optional<Line> findById(long id) {
        String query = "SELECT ID, NAME, COLOR FROM LINE WHERE ID = ?";
        try {
            Line line = jdbcTemplate.queryForObject(query, ROW_MAPPER, id);
            return Optional.ofNullable(line);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return Optional.empty();
        }
    }

    public void update(long id, String name, String color) {
        String query = "UPDATE LINE SET name = ?, color = ? WHERE ID = ?";
        try {
            int affectedRowCounts = jdbcTemplate.update(query, name, color, id);
            validateId(affectedRowCounts);
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new SubwayException(ExceptionStatus.DUPLICATED_NAME);
        }
    }

    private void validateId(int affectedRowCounts) {
        if (affectedRowCounts == ROW_COUNTS_FOR_ID_NOT_FOUND) {
            throw new SubwayException(ExceptionStatus.ID_NOT_FOUND);
        }
    }

    public void deleteById(long id) {
        String query = "DELETE FROM LINE WHERE ID = ?";
        int affectedRowCounts = jdbcTemplate.update(query, id);
        validateId(affectedRowCounts);
    }
}
