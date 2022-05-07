package wooteco.subway.dao;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.entity.StationEntity;

@Repository
public class StationRepository extends AbstractRepository<StationEntity, Long> {

    private final JdbcTemplate jdbcTemplate;

    public StationRepository(JdbcTemplate jdbcTemplate, DataSource dataSource,
                             NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, dataSource, namedParameterJdbcTemplate);

        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByName(String name) {
        final String sql = "SELECT EXISTS (SELECT name FROM station WHERE name = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, name));
    }
}
