package wooteco.subway.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

import java.util.HashMap;
import java.util.Map;

@Repository
public class SectionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SectionDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        String sql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) " +
                "VALUES (:lineId, :upStationId, :downStationId, :distance)";

        Map<String, Object> params = new HashMap<>();
        params.put("lineId", section.getLineId());
        params.put("upStationId", section.getUpStationId());
        params.put("downStationId", section.getDownStationId());
        params.put("distance", section.getDistance());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        long sectionId = keyHolder.getKey().longValue();

        return new Section(sectionId, section.getDistance(), section.getLineId(), section.getUpStationId(), section.getDownStationId());
    }
}
