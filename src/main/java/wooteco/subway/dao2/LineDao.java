package wooteco.subway.dao2;

import java.util.Collections;
import java.util.List;
import org.springframework.dao.DataAccessException;
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

        try {
            return jdbcTemplate.queryForObject(sql, paramSource, lineRowMapper);
        } catch (DataAccessException e) {
            throw new NotFoundException("해당되는 노선은 존재하지 않습니다.");
        }
    }

    public Line save(Line line) {
        final String sql = "INSERT INTO line(name, color) VALUES(:name, :color)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(line);
        try {
            jdbcTemplate.update(sql, paramSource, keyHolder);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("중복되는 이름의 지하철 노선이 존재합니다.");
        }
        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    public void update(Line line) {
        final String sql = "UPDATE line SET name = :name, color = :color "
                + "WHERE id = :id";

        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(line);
        try {
            validateUpdateResult(jdbcTemplate.update(sql, paramSource));
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("중복되는 이름의 지하철 노선이 존재합니다.");
        }
    }

    public void deleteById(Long id) {
        final String sql = "DELETE FROM line WHERE id = :id";

        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", id);

        validateUpdateResult(jdbcTemplate.update(sql, paramSource));
    }

    private void validateUpdateResult(int effectedRowCount) {
        if (effectedRowCount == 0) {
            throw new IllegalArgumentException("해당되는 노선은 존재하지 않습니다.");
        }
    }

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) ->
            new Line(resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color"));
}
