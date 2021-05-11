package wooteco.subway.dao;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.line.Line;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class LineDao {
    private static final RowMapper<Line> BASIC_LINE_ROW_MAPPER = (resultSet, rowNumber) -> {
        long id = resultSet.getLong("ID");
        String name = resultSet.getString("NAME");
        String color = resultSet.getString("COLOR");
        return Line.builder().id(id).name(name).color(color).build();
    };
    private static final int ROW_COUNTS_FOR_ID_NOT_FOUND = 0;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public LineDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
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
        String query = "SELECT ID, NAME, COLOR FROM LINE WHERE ID = :ID";
        try {
            Line line = jdbcTemplate.queryForObject(query, Collections.singletonMap("ID", id), BASIC_LINE_ROW_MAPPER);
            return Optional.ofNullable(line);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return Optional.empty();
        }
    }

    public void update(Line line) {
        String query = "UPDATE LINE SET NAME = :NAME, COLOR = :COLOR WHERE ID = :ID";
        try {
            Map<String, Object> parameters = generateParameters(line);
            int affectedRowCounts = jdbcTemplate.update(query, parameters);
            validateId(affectedRowCounts);
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new SubwayException(ExceptionStatus.DUPLICATED_NAME);
        }
    }

    private Map<String, Object> generateParameters(Line line) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("NAME", line.getName());
        parameters.put("COLOR", line.getColor());
        parameters.put("ID", line.getId());
        return parameters;
    }

    private void validateId(int affectedRowCounts) {
        if (affectedRowCounts == ROW_COUNTS_FOR_ID_NOT_FOUND) {
            throw new SubwayException(ExceptionStatus.ID_NOT_FOUND);
        }
    }

    public void deleteById(long id) {
        String query = "DELETE FROM LINE WHERE ID = :ID";
        int affectedRowCounts = jdbcTemplate.update(query, Collections.singletonMap("ID", id));
        validateId(affectedRowCounts);
    }
}
