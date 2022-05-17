package wooteco.subway.infra.dao;


import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.infra.dao.entity.SectionEntity;

@Repository
public class SectionDao extends AbstractDao<SectionEntity, Long> {

    private static final String SECTION_STATION_JOIN_QUERY =
            "SELECT a.id, "
                    + "a.line_id, "
                    + "a.up_station_id, "
                    + "b.name as up_station_name, "
                    + "a.down_station_id, "
                    + "c.name as down_station_name, "
                    + "a.distance "
                    + "FROM section a "
                    + "JOIN station b ON a.up_station_id = b.id "
                    + "JOIN station c ON a.down_station_id = c.id";

    private static final RowMapper<SectionEntity> SECTION_ENTITY_MAPPER = (rs, rowNum) ->
            new SectionEntity(
                    rs.getLong("id"),
                    rs.getLong("line_id"),
                    rs.getLong("up_station_id"),
                    rs.getString("up_station_name"),
                    rs.getLong("down_station_id"),
                    rs.getString("down_station_name"),
                    rs.getInt("distance")
            );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource,
                      NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, dataSource, namedParameterJdbcTemplate);
        this.jdbcTemplate = jdbcTemplate;

        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public void save(List<SectionEntity> sectionEntities) {
        final SectionEntity sectionEntity = sectionEntities.get(0);
        deleteAllByLineId(sectionEntity.getLineId());

        final MapSqlParameterSource[] mapSqlParameterSources = sectionEntities
                .stream()
                .map(entity -> new MapSqlParameterSource()
                        .addValue("line_id", entity.getLineId())
                        .addValue("up_station_id", entity.getUpStationId())
                        .addValue("down_station_id", entity.getDownStationId())
                        .addValue("distance", entity.getDistance())
                )
                .toArray(MapSqlParameterSource[]::new);

        simpleJdbcInsert.executeBatch(mapSqlParameterSources);
    }

    private void deleteAllByLineId(Long lineId) {
        final String sql = "DELETE FROM section WHERE line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }

    @Override
    public List<SectionEntity> findAll() {
        final String sql = SECTION_STATION_JOIN_QUERY;
        return jdbcTemplate.query(sql, SECTION_ENTITY_MAPPER);
    }

    public List<SectionEntity> findSectionsByLineId(Long lineId) {
        final String sql = SECTION_STATION_JOIN_QUERY + " WHERE a.line_id = ?";
        return jdbcTemplate.query(sql, SECTION_ENTITY_MAPPER, lineId);
    }
}
