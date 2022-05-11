package wooteco.subway.dao;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.entity.SectionEntity;

@Repository
public class SectionDao {
    private static final RowMapper<SectionEntity> SECTION_ENTITY_MAPPER = (resultSet, rowNum) -> SectionEntity.of(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance")
    );

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleInsert;

    public SectionDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
    }

    public SectionEntity save(SectionEntity sectionEntity) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(sectionEntity);
        Long id = simpleInsert.executeAndReturnKey(parameters).longValue();
        return SectionEntity.of(id, sectionEntity);
    }

    public void saveAll(List<SectionEntity> sectionEntities) {
        sectionEntities.forEach(this::save);
    }

    public List<SectionEntity> findByLineId(Long lineId) {
        String sql = "SELECT * FROM SECTION WHERE line_id = :lineId";
        SqlParameterSource parameters = new MapSqlParameterSource("lineId", lineId);
        return jdbcTemplate.query(sql, parameters, SECTION_ENTITY_MAPPER);
    }

    public void delete(SectionEntity sectionEntity) {
        String sql = "DELETE FROM SECTION WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", sectionEntity.getId());
        jdbcTemplate.update(sql, parameters);
    }

    public void deleteALl(List<SectionEntity> sectionEntities) {
        sectionEntities.forEach(this::delete);
    }
}
