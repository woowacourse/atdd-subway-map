package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.dto.SectionDto;

@Repository
public class JdbcSectionDao implements SectionDao {

    private static final String TABLE_NAME = "SECTION";
    private static final String KEY_NAME = "id";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.insertActor = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(KEY_NAME);
    }

    @Override
    public SectionDto save(SectionDto sectionDto) {
        Long id = insertActor.executeAndReturnKey(
                        Map.of("line_id", sectionDto.getLineId(),
                                "up_station_id", sectionDto.getUpStationId(),
                                "down_station_id", sectionDto.getDownStationId(),
                                "distance", sectionDto.getDistance()))
                .longValue();
        return findById(id);
    }

    @Override
    public int saveAll(List<SectionDto> sectionDtos) {
        int[] inserted = insertActor.executeBatch(SqlParameterSourceUtils.createBatch(sectionDtos));
        return inserted.length;
    }

    @Override
    public SectionDto findById(Long id) {
        String sql = "select * from SECTION where id = :id";
        return jdbcTemplate.queryForObject(sql, Map.of("id", id), mapper());
    }

    @Override
    public List<SectionDto> findByLineId(Long id) {
        String sql = "select * from SECTION where line_id = :id";
        return jdbcTemplate.query(sql, Map.of("id", id), mapper());
    }

    private RowMapper<SectionDto> mapper() {
        return (resultSet, rowNum) ->
                new SectionDto(
                        resultSet.getLong("id"),
                        resultSet.getLong("line_id"),
                        resultSet.getLong("up_station_id"),
                        resultSet.getLong("down_station_id"),
                        resultSet.getInt("distance")
                );
    }
}
