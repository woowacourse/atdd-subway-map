package wooteco.subway.infra.dao;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.infra.entity.LineEntity;

@Repository
public class LineDao extends AbstractDao<LineEntity, Long> {

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate, DataSource dataSource,
                   NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, dataSource, namedParameterJdbcTemplate);

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<LineEntity> findAll() {
        final String sql = "SELECT l.id, l.name, l.color, s.id, s.line_id, s.up_station_id, s.down_station_id, s.distance FROM line JOIN section ON line.id = section.line_id";
        return null;
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
