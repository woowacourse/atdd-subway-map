package wooteco.subway.repository.dao;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.repository.entity.LineEntity;
import wooteco.subway.repository.entity.SectionEntity;

@Repository
public class JdbcSectionDao implements SectionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcSectionDao(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SectionEntity save(final SectionEntity sectionEntity) {
        final String sql = "INSERT INTO SECTION(line_id, up_station_id, down_station_id, distance) "
                + "VALUES(:lineId, :upStationId, :downStationId, :distance)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final SqlParameterSource source = new BeanPropertySqlParameterSource(sectionEntity);
        jdbcTemplate.update(sql, source, keyHolder);
        if (keyHolder.getKey() == null) {
            throw new RuntimeException("[ERROR] SECTION 테이블에 레코드 추가 후에 key가 반환되지 않았습니다.");
        }
        return new SectionEntity(
                keyHolder.getKey().longValue(),
                sectionEntity.getLineId(),
                sectionEntity.getUpStationId(),
                sectionEntity.getDownStationId(),
                sectionEntity.getDistance()
        );
    }
}
