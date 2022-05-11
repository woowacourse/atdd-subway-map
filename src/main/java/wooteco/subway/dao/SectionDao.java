package wooteco.subway.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
public class SectionDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public SectionDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Section save(final Section section) {
        final String sql = "insert into section(line_id, up_station_id, down_station_id, distance) values(:lineId, :upStationId, :downStationId, :distance)";

        final Map<String, Object> params = new HashMap<>();
        params.put("lineId", section.getLineId());
        params.put("upStationId", section.getUpStation().getId());
        params.put("downStationId", section.getDownStation().getId());
        params.put("distance", section.getDistance());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        final long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Section(id, section.getUpStation(), section.getDownStation(), section.getDistance(), section.getLineId());
    }
}
