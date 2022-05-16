package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import wooteco.subway.service.dto.SectionDto;

@Component
public class SectionDao implements CommonSectionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleInsert;

    public SectionDao(final NamedParameterJdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public long save(final Long lineId, final SectionDto section) {
        final Map<String, Object> params = new HashMap<>();
        params.put("line_id", lineId);
        params.put("up_station_id", section.getUpStationId());
        params.put("down_station_id", section.getDownStationId());
        params.put("distance", section.getDistance());
        return simpleInsert.executeAndReturnKey(params).longValue();
    }

    @Override
    public List<SectionDto> findAllByLineId(final Long lineId) {
        final String sql = "select line_id, up_station_id, down_station_id, distance from SECTION where line_id=:lineId";
        final SqlParameterSource parameterSource = new MapSqlParameterSource(Map.of("lineId", lineId));
        return jdbcTemplate.query(sql, parameterSource, (resultSet, rowNum) -> {
            return new SectionDto(resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance"),
                    resultSet.getLong("line_id"));
        });
    }

    @Override
    public int deleteById(final Long lineId, final Long stationId) {
        final String sql = "delete from SECTION where line_id=:lineId and up_station_id=:stationId or down_station_id=:stationId";
        return jdbcTemplate.update(sql, Map.of("lineId", lineId, "stationId", stationId));
    }
}
