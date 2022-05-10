package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.entity.LineEntity;

@Repository
public class LineDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public LineDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public LineEntity save(LineEntity lineEntity) {
        String sql = "insert into LINE (name, color) values (:name, :color)";

        Map<String, Object> params = new HashMap<>();
        params.put("name", lineEntity.getName());
        params.put("color", lineEntity.getColor());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        return new LineEntity.Builder(lineEntity.getName(), lineEntity.getColor())
                .id(Objects.requireNonNull(keyHolder.getKey()).longValue())
                .build();
    }

    public List<LineEntity> findAll() {
        String sql = "select * from LINE";

        return namedParameterJdbcTemplate.query(sql,
                (rs, rowNum) -> new LineEntity.Builder(rs.getString("name"), rs.getString("color"))
                        .id(rs.getLong("id"))
                        .build()
        );
    }

    public Optional<LineEntity> findById(Long id) {
        String sql = "select * from LINE where id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        List<LineEntity> queryResult = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource(params),
                (rs, rowNum) -> new LineEntity.Builder(rs.getString("name"), rs.getString("color"))
                        .id(rs.getLong("id"))
                        .build());
        return Optional.ofNullable(DataAccessUtils.singleResult(queryResult));
    }

    public Optional<LineEntity> findByName(String name) {
        String sql = "select * from LINE where name = :name";

        Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        List<LineEntity> queryResult = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource(params),
                (rs, rowNum) -> new LineEntity.Builder(rs.getString("name"), rs.getString("color"))
                        .id(rs.getLong("id"))
                        .build());
        return Optional.ofNullable(DataAccessUtils.singleResult(queryResult));
    }

    public int update(Long id, LineEntity lineEntity) {
        String sql = "update LINE set name = :name, color = :color where id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("name", lineEntity.getName());
        params.put("color", lineEntity.getColor());

        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }

    public int deleteById(Long id) {
        String sql = "delete from LINE where id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }
}
