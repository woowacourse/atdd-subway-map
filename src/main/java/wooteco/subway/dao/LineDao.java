package wooteco.subway.dao;

import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

@Repository
public class LineDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<Line> eventRowMapper = (resultSet, rowNum)
            -> new Line(resultSet.getLong("id")
            , resultSet.getString("name")
            , resultSet.getString("color"));

    public LineDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Line line) {
        String insertSql = "insert into LINE (name, color) values (:name, :color)";
        SqlParameterSource source = new BeanPropertySqlParameterSource(line);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(insertSql, source, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public Boolean existByName(String name) {
        String selectSql = "select EXISTS (select * from LINE where name = :name)";
        SqlParameterSource source = new MapSqlParameterSource("name", name);
        return Objects.requireNonNull(jdbcTemplate.queryForObject(selectSql, source, Boolean.class));
    }

    public Boolean existByColor(String color) {
        String selectSql = "select EXISTS (select * from LINE where color = :color)";
        SqlParameterSource source = new MapSqlParameterSource("color", color);
        return Objects.requireNonNull(jdbcTemplate.queryForObject(selectSql, source, Boolean.class));
    }

    public Line findById(Long id) {
        String selectSql = "select * from LINE where id = :id";
        SqlParameterSource source = new MapSqlParameterSource("id", id);
        return jdbcTemplate.queryForObject(selectSql, source, eventRowMapper);
    }

    public List<Line> findAll() {
        String selectSql = "select * from LINE";
        return jdbcTemplate.query(selectSql, eventRowMapper);
    }

    public void update(Long id, LineRequest lineRequest) {
        String updateSql = "update LINE set name=:name, color=:color where id=:id";
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("id", id);
        source.addValue("color", lineRequest.getColor());
        source.addValue("name", lineRequest.getName());
        jdbcTemplate.update(updateSql, source);
    }

    public void deleteById(Long id) {
        String deleteSql = "delete from LINE where id=:id";
        SqlParameterSource source = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(deleteSql, source);
    }
}
