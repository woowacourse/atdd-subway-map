package wooteco.subway.repository.dao.jdbc;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.dao.entity.line.LineEntity;

@Component
public class JdbcLineDao implements LineDao {

    private static final RowMapper<LineEntity> ROW_MAPPER =
            (resultSet, rowNum) -> new LineEntity(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color")
            );

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public JdbcLineDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("Line")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Long save(LineEntity lineEntity) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(lineEntity);
        return jdbcInsert.executeAndReturnKey(parameters)
                .longValue();
    }

    @Override
    public List<LineEntity> findAll() {
        String query = "SELECT id, name, color from Line";
        return jdbcTemplate.query(query, ROW_MAPPER);
    }

    @Override
    public Optional<LineEntity> findById(Long id) {
        String query = "SELECT id, name, color from Line WHERE id=(:id)";
        try {
            SqlParameterSource parameters = new MapSqlParameterSource("id", id);
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, parameters, ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Boolean existsById(Long id) {
        String query = "SELECT EXISTS(SELECT id FROM Line WHERE id=(:id)) as existable";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        return jdbcTemplate.queryForObject(query, parameters,
                (resultSet, rowNum) -> resultSet.getBoolean("existable"));
    }

    @Override
    public Boolean existsByName(String name) {
        String query = "SELECT EXISTS(SELECT id FROM Line WHERE name=(:name)) as existable";
        SqlParameterSource parameters = new MapSqlParameterSource("name", name);
        return jdbcTemplate.queryForObject(query, parameters,
                (resultSet, rowNum) -> resultSet.getBoolean("existable"));
    }

    @Override
    public Boolean existsByColor(String color) {
        String query = "SELECT EXISTS(SELECT id FROM Line WHERE color=(:color)) as existable";
        SqlParameterSource parameters = new MapSqlParameterSource("color", color);
        return jdbcTemplate.queryForObject(query, parameters,
                (resultSet, rowNum) -> resultSet.getBoolean("existable"));
    }

    @Override
    public void update(LineEntity lineEntity) {
        String query = "UPDATE Line SET name=(:name), color=(:color) WHERE id=(:id)";
        SqlParameterSource parameters = new MapSqlParameterSource("id", lineEntity.getId())
                .addValue("name", lineEntity.getName())
                .addValue("color", lineEntity.getColor());
        jdbcTemplate.update(query, parameters);
    }

    @Override
    public void remove(Long id) {
        String query = "DELETE FROM Line WHERE id=(:id)";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(query, parameters);
    }
}
