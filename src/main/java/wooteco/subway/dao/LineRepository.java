package wooteco.subway.dao;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.entity.LineEntity;

@Repository
public class LineRepository extends AbstractRepository<LineEntity, Long> {

    public LineRepository(JdbcTemplate jdbcTemplate, DataSource dataSource,
                          NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, dataSource, namedParameterJdbcTemplate);
    }
}
