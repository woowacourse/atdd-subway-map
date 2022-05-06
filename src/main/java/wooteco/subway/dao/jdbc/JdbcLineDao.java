package wooteco.subway.dao.jdbc;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@Repository
public class JdbcLineDao implements LineDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

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
        return jdbcTemplate.query(query,
                (resultSet, rowNum) -> new Line(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("color")
                ));
    }

    @Override
    public Line findById(Long id) {
        String query = "SELECT id, name, color from Line WHERE id=(:id)";
        try {
            SqlParameterSource parameters = new MapSqlParameterSource("id", id);
            return jdbcTemplate.queryForObject(query, parameters,
                    (resultSet, rowNum) -> new Line(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("color")
                    ));
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("해당 id에 맞는 지하철 노선이 없습니다.");
        }
    }

    @Override
    public Boolean existsByName(String name) {
        String query = "SELECT COUNT(*) as num FROM Line WHERE name=(:name)";
        SqlParameterSource parameters = new MapSqlParameterSource("name", name);
        int count = jdbcTemplate.queryForObject(query, parameters,
                (resultSet, rowNum) -> resultSet.getInt("num"));
        return count != 0;
    }

    @Override
    public Boolean existsByColor(String color) {
        String query = "SELECT COUNT(*) as num FROM Line WHERE color=(:color)";
        SqlParameterSource parameters = new MapSqlParameterSource("color", color);
        int count = jdbcTemplate.queryForObject(query, parameters,
                (resultSet, rowNum) -> resultSet.getInt("num"));
        return count != 0;
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