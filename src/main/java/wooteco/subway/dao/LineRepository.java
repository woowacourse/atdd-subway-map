package wooteco.subway.dao;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.utils.exception.ExceptionMessages;
import wooteco.subway.utils.exception.IdNotFoundException;
import wooteco.subway.utils.exception.NameDuplicatedException;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class LineRepository {

    private static final int NO_ROW = 0;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public LineRepository(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    public Line save(final Line line) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", line.getName())
                .addValue("color", line.getColor());

        try {
            long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
            return new Line(id, line.getName(), line.getColor());
        } catch (DuplicateKeyException e) {
            throw new NameDuplicatedException(ExceptionMessages.NAME_DUPLICATE_MESSAGE);
        }
    }

    public List<Line> findAll() {
        String sql = "SELECT * FROM line";
        return namedParameterJdbcTemplate.query(sql, rowMapper());
    }

    public Line findById(final Long id) {
        String sql = "SELECT * FROM line WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, parameters, rowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new IdNotFoundException(ExceptionMessages.NO_ID_MESSAGE);
        }
    }

    public Optional<Line> findByName(final String name) {
        String sql = "SELECT * FROM line WHERE name = :name";
        SqlParameterSource parameters = new MapSqlParameterSource("name", name);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, parameters, rowMapper()));
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    private RowMapper<Line> rowMapper() {
        return (resultSet, rowNum) -> {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            String color = resultSet.getString("color");
            return new Line(id, name, color);
        };
    }

    public void update(final Line line) {
        String sql = "UPDATE line SET name = :name, color = :color WHERE id = :id";
        SqlParameterSource nameParameters = new BeanPropertySqlParameterSource(line);
        try {
            namedParameterJdbcTemplate.update(sql, nameParameters);
        } catch (DuplicateKeyException e) {
            throw new NameDuplicatedException(ExceptionMessages.NAME_DUPLICATE_MESSAGE);
        } catch (EmptyResultDataAccessException e) {
            throw new IdNotFoundException(ExceptionMessages.NO_ID_MESSAGE);
        }

    }

    public void deleteById(final Long id) {
        String sql = "DELETE FROM line WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        int rowCounts = namedParameterJdbcTemplate.update(sql, parameters);
        if (rowCounts == NO_ROW) {
            throw new IdNotFoundException(ExceptionMessages.NO_ID_MESSAGE);
        }
    }
}
