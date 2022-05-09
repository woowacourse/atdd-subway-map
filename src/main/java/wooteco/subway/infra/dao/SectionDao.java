package wooteco.subway.infra.dao;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.infra.entity.SectionEntity;

@Repository
public class SectionDao extends AbstractDao<SectionEntity, Long> {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource,
                      NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, dataSource, namedParameterJdbcTemplate);
        this.jdbcTemplate = jdbcTemplate;
    }

    public int deleteByLineIdAndStationId(Long lineId, Long sectionId) {
        final String sql = "DELETE FROM section WHERE line_id = ? AND station_id = ?";
        return jdbcTemplate.update(sql, lineId, sectionId);
    }
}
