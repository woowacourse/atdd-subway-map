package wooteco.subway.infra.dao;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.infra.dao.entity.StationEntity;

@Repository
public class StationDao extends AbstractDao<StationEntity, Long> {

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate, DataSource dataSource,
                      NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, dataSource, namedParameterJdbcTemplate);
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByName(String name) {
        final String sql = "SELECT EXISTS (SELECT name FROM station WHERE name = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, name));
    }
}
