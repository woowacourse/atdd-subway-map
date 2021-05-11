package wooteco.subway.dao;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.line.Line;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;

import java.util.List;
import java.util.Optional;

@Repository
public class LineDao {
    private static final RowMapper<Line> BASIC_LINE_ROW_MAPPER = (resultSet, rowNumber) -> {
        long id = resultSet.getLong("ID");
        String name = resultSet.getString("NAME");
        String color = resultSet.getString("COLOR");
        return Line.builder()
                .id(id)
                .name(name)
                .color(color)
                .build();
    };
    private static final int ROW_COUNTS_FOR_ID_NOT_FOUND = 0;

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("ID");
    }

    public long save(Line line) {
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(line);
        try {
            return simpleJdbcInsert.executeAndReturnKey(sqlParameterSource)
                    .longValue();
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new SubwayException(ExceptionStatus.DUPLICATED_NAME);
        }
    }

    public List<Line> findAll() {
        String query = "SELECT ID, NAME, COLOR FROM LINE";
        return jdbcTemplate.query(query, BASIC_LINE_ROW_MAPPER);
    }

    public Optional<Line> findById(long id) {
        String query = "SELECT ID, NAME, COLOR FROM LINE WHERE ID = ?";
        try {
            Line line = jdbcTemplate.queryForObject(query, BASIC_LINE_ROW_MAPPER, id);
            return Optional.ofNullable(line);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return Optional.empty();
        }
    }

    public void update(Line line) {
        String query = "UPDATE LINE SET name = ?, color = ? WHERE ID = ?";
        try {
            int affectedRowCounts = jdbcTemplate.update(query, line.getName(), line.getColor(), line.getId());
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
