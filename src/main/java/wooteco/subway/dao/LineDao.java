package wooteco.subway.dao;

import java.util.Collections;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.NotFoundException;

@Repository
public class LineDao {

    private static final String LINE_NOT_FOUND_EXCEPTION_MESSAGE = "해당되는 노선은 존재하지 않습니다.";
    private static final String DUPLICATE_NAME_EXCEPTION_MESSAGE = "중복되는 이름의 지하철 노선이 존재합니다.";

    private static final RowMapper<Line> lineRowMapper = (resultSet, rowNum) ->
            new Line(resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color"));
    
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public LineDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";

        List<Line> lines = jdbcTemplate.query(sql, new EmptySqlParameterSource(), lineRowMapper);
        return Collections.unmodifiableList(lines);
    }

    public Line findById(Long id) {
        final String sql = "SELECT * FROM line WHERE id = :id";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", id);

        return new StatementExecutor<>(() -> jdbcTemplate.queryForObject(sql, paramSource, lineRowMapper))
                .executeOrThrow(() -> new NotFoundException(LINE_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    public Line save(Line line) {
        final String sql = "INSERT INTO line(name, color) VALUES(:name, :color)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(line);

        new StatementExecutor<>(() -> jdbcTemplate.update(sql, paramSource, keyHolder))
                .executeOrThrow(() -> new IllegalArgumentException(DUPLICATE_NAME_EXCEPTION_MESSAGE));
        Number generatedId = keyHolder.getKey();
        return new Line(generatedId.longValue(), line.getName(), line.getColor());
    }

    public void update(Line line) {
        final String sql = "UPDATE line SET name = :name, color = :color WHERE id = :id";
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(line);

        new StatementExecutor<>(() -> jdbcTemplate.update(sql, paramSource))
                .updateOrThrow(() -> new IllegalArgumentException(DUPLICATE_NAME_EXCEPTION_MESSAGE))
                .throwOnNonEffected(() -> new IllegalArgumentException(LINE_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    public void deleteById(Long id) {
        final String sql = "DELETE FROM line WHERE id = :id";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", id);

        new StatementExecutor<>(() -> jdbcTemplate.update(sql, paramSource))
                .update()
                .throwOnNonEffected(() -> new IllegalArgumentException(LINE_NOT_FOUND_EXCEPTION_MESSAGE));
    }
}
