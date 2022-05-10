package wooteco.subway.dao;

import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SubwaySectionDao implements SectionDao<Section> {
    
    private final JdbcTemplate jdbcTemplate;

    public SubwaySectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section save(Section section) {
        final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("section").usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("line_id", section.getLine().getId());
        parameters.put("up_station_id", section.getUpStation().getId());
        parameters.put("down_station_id", section.getDownStation().getId());
        parameters.put("distance", section.getDistance());

        final Number number = simpleJdbcInsert.executeAndReturnKey(parameters);
        return new Section(number.longValue(), section.getLine(), section.getUpStation(), section.getDownStation(),
                section.getDistance());
    }

    @Override
    public int deleteSection(Long lineId, Long stationId) {
        String sql = "DELETE FROM section "
                + "WHERE line_id=? AND (up_station_id=? OR down_station_id=?)";
        return jdbcTemplate.update(sql, lineId, stationId, stationId);
    }
}
