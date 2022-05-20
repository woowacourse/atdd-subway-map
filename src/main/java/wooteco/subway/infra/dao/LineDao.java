package wooteco.subway.infra.dao;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.infra.dao.entity.LineEntity;

@Repository
public class LineDao extends AbstractDao<LineEntity, Long> {

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate, DataSource dataSource,
                   NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, dataSource, namedParameterJdbcTemplate);

        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByName(String name) {
        final String sql = "SELECT EXISTS (SELECT name FROM line WHERE name = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, name));
    }

    public boolean existsByColor(String color) {
        final String sql = "SELECT EXISTS (SELECT color FROM line WHERE color = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, color));
    }

    public boolean existSameNameWithDifferentId(String name, Long id) {
        final String sql = "SELECT EXISTS (SELECT 1 FROM line WHERE name = ? AND id != ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, name, id));
    }
}
