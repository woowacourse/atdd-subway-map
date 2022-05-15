package wooteco.subway.dao;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import java.util.*;

@Repository
public class LineDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<Line> resultMapper = (rs, rowNum) -> new Line(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("color")
    );

    public LineDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Line save(final Line line) {
        final String sql = "insert into LINE (name, color) values (:name, :color)";

        final Map<String, Object> params = new HashMap<>();
        params.put("name", line.getName());
        params.put("color", line.getColor());

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        return new Line(Objects.requireNonNull(keyHolder.getKey()).longValue(), line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        final String sql = "select * from LINE";

        return namedParameterJdbcTemplate.query(sql,
                (rs, rowNum) -> new Line(rs.getLong("id"), rs.getString("name"), rs.getString("color")));
    }

    public Optional<Line> findById(final Long id) {
        final String sql = "select * from LINE where id=:id";

        final Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        final List<Line> queryResult = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource(params), resultMapper);
        return Optional.ofNullable(DataAccessUtils.singleResult(queryResult));
    }

    public Optional<Line> findByName(final String name) {
        final String sql = "select * from LINE where name=:name";

        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        final List<Line> queryResult = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource(params), resultMapper);
        return Optional.ofNullable(DataAccessUtils.singleResult(queryResult));
    }

    public int update(final Long id, final Line newLine) {
        final String sql = "update LINE set name=:name, color=:color where id=:id";

        final Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("name", newLine.getName());
        params.put("color", newLine.getColor());

        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }

    public int deleteById(final Long id) {
        final String sql = "delete from LINE where id = :id";

        final Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }
}
