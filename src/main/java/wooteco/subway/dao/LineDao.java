package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.entity.LineEntity;

@Repository
public class LineDao {

    private static final RowMapper<LineEntity> ROW_MAPPER = (resultSet, rowNum) ->
            new LineEntity(resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color"));

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public LineDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<LineEntity> findAll() {
        final String sql = "SELECT * FROM line";

        return jdbcTemplate.query(sql, new EmptySqlParameterSource(), ROW_MAPPER);
    }

    public Optional<LineEntity> findById(Long id) {
        final String sql = "SELECT * FROM line WHERE id = :id";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", id);

        return jdbcTemplate.query(sql, paramSource, ROW_MAPPER)
                .stream()
                .findFirst();
    }

    public Optional<LineEntity> findByName(String name) {
        final String sql = "SELECT * FROM line WHERE name = :name";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("name", name);

        return jdbcTemplate.query(sql, paramSource, ROW_MAPPER)
                .stream()
                .findFirst();
    }

    public LineEntity save(LineEntity lineEntity) {
        final String sql = "INSERT INTO line(name, color) VALUES(:name, :color)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(lineEntity);

        jdbcTemplate.update(sql, paramSource, keyHolder);
        Number generatedId = keyHolder.getKey();
        return new LineEntity(generatedId.longValue(), lineEntity.getName(), lineEntity.getColor());
    }

    public void update(LineEntity lineEntity) {
        final String sql = "UPDATE line SET name = :name, color = :color WHERE id = :id";
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(lineEntity);

        jdbcTemplate.update(sql, paramSource);
    }

    public void deleteById(Long id) {
        final String sql = "DELETE FROM line WHERE id = :id";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", id);

        jdbcTemplate.update(sql, paramSource);
    }
}
