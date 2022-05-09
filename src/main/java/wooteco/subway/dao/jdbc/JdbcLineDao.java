package wooteco.subway.dao.jdbc;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.line.Line;

@Repository
public class JdbcLineDao implements LineDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final RowMapper<Line> lineRowMapper =
            (resultSet, rowNum) -> new Line(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color")
            );

    public JdbcLineDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("Line")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Long save(Line line) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        return jdbcInsert.executeAndReturnKey(parameters)
                .longValue();
    }

    @Override
    public List<Line> findAll() {
        String query = "SELECT id, name, color from Line";
        return jdbcTemplate.query(query, lineRowMapper);
    }

    @Override
    public Line findById(Long id) {
        String query = "SELECT id, name, color from Line WHERE id=(:id)";
        try {
            SqlParameterSource parameters = new MapSqlParameterSource("id", id);
            return jdbcTemplate.queryForObject(query, parameters, lineRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("해당 id에 맞는 지하철 노선이 없습니다.");
        }
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
    public void update(Long id, String name, String color) {
        String query = "UPDATE Line SET name=(:name), color=(:color) WHERE id=(:id)";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id)
                .addValue("name", name)
                .addValue("color", color);
        jdbcTemplate.update(query, parameters);
    }

    @Override
    public void remove(Long id) {
        String query = "DELETE FROM Line WHERE id=(:id)";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(query, parameters);
    }
}
