package wooteco.subway.dao;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.entity.SectionEntity;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
    }

    public SectionEntity save(SectionEntity sectionEntity) {
        final SqlParameterSource parameters = new BeanPropertySqlParameterSource(sectionEntity);
        final Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new SectionEntity(id, sectionEntity.getUpStationId(), sectionEntity.getDownStationId(), sectionEntity.getLineId(),
                sectionEntity.getDistance());
    }
}
