package wooteco.subway.dao;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class LineDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public LineDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private static Line rowMapper(ResultSet rs, int row) throws SQLException {
        return new Line(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("color")
        );
    }

    public Line save(final Line line) {
        final String sql = "INSERT INTO LINE (name, color) VALUES (:name, :color)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final Map<String, Object> params = new HashMap<>();
        params.put("name", line.getName());
        params.put("color", line.getColor());

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        final long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return new Line(id, line.getName(), line.getColor());
    }

    public Optional<Line> findById(final Long id) {
        final String sql = "SELECT id, name, color FROM LINE WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        final List<Line> lines = namedParameterJdbcTemplate.query(sql, params, LineDao::rowMapper);

        return Optional.ofNullable(DataAccessUtils.singleResult(lines));
    }

    public List<Line> findAll() {
        final String sql = "SELECT id, name, color FROM LINE";
        return namedParameterJdbcTemplate.query(sql, LineDao::rowMapper);
    }

    public void update(final Long id, final Line line) {
        final String sql = "UPDATE LINE SET name = :name, color = :color WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("name", line.getName());
        params.put("color", line.getColor());

        namedParameterJdbcTemplate.update(sql, params);
    }

    public int deleteById(final Long id) {
        final String sql = "DELETE FROM LINE WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        return namedParameterJdbcTemplate.update(sql, params);
    }

    public boolean existByName(final String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE NAME = :name)";
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(sql, params, Boolean.class));
    }

    public boolean existById(final Long id) {
        final String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE ID = :id)";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(sql, params, Boolean.class));
    }
}
