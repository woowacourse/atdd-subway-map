package wooteco.subway.dao;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.entity.LineEntity;

@Repository
public class LineRepository extends AbstractRepository<LineEntity, Long> {

    private final JdbcTemplate jdbcTemplate;

    public LineRepository(JdbcTemplate jdbcTemplate, DataSource dataSource,
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
}
