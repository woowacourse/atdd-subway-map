package wooteco.subway.dao;

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

@Repository
public class LineDao {

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

        return jdbcTemplate.queryForObject(sql, paramSource, lineRowMapper);
    }

    public Line save(Line line) {
        final String sql = "INSERT INTO line(name, color) VALUES(:name, :color)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(line);

        jdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"ID"});
        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    public void update(Line line) {
        final String sql = "UPDATE line SET name = :name, color = :color "
                + "WHERE id = :id";
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(line);

        jdbcTemplate.update(sql, paramSource);
    }

    public void deleteById(Long id) {
        final String sql = "DELETE FROM line WHERE id = :id";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", id);

        jdbcTemplate.update(sql, paramSource);
    }

    public boolean existByName(String name) {
        final String sql = "SELECT EXISTS (SELECT 1 FROM line WHERE name = :name)";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("name", name);

        return jdbcTemplate.queryForObject(sql, paramSource, Integer.class) != 0;
    }

    public boolean existById(Long id) {
        final String sql = "SELECT EXISTS (SELECT 1 FROM line WHERE id = :id)";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", id);

        return jdbcTemplate.queryForObject(sql, paramSource, Integer.class) != 0;
    }
}
