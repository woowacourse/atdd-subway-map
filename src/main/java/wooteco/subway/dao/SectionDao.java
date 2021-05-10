package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.service.dto.SimpleSectionDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SectionDao {

    private static final RowMapper<Section> SECTION_MAPPER = (resultSet, rowNum) -> new Section(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance")
    );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public SectionDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public Long insert(Long id, SimpleSectionDto section) {
        Map<String, Object> params = new HashMap<>(4);
        params.put("line_id", id);
        params.put("up_station_id", section.getUpStationId());
        params.put("down_station_id", section.getDownStationId());
        params.put("distance", section.getDistance());
        return simpleJdbcInsert.executeAndReturnKey(params).longValue();
    }

    public List<Section> findAllByLineId(Long lineId) {
        String query = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(query, SECTION_MAPPER, lineId);
    }
}
