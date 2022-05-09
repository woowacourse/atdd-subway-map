package wooteco.subway.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SectionDao {

    private static final RowMapper<Section> rowMapper = (rs, rowNum)
            -> new Section(rs.getLong("id"),
            rs.getInt("distance"),
            rs.getLong("line_id"),
            rs.getLong("up_station_id"),
            rs.getLong("down_station_id")
            );

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

    //이걸 이렇게 모두 다 조회해오는게 괜찮을까?
    public List<Section> findAll() {
        String sql = "SELECT * FROM section";
        return jdbcTemplate.query(sql, new MapSqlParameterSource(), rowMapper);
    }

    public List<Section> findByLineId(Long lineId) {
        String sql = "SELECT * FROM section WHERE line_id = :lineId";
        Map<String, Object> params = new HashMap<>();
        params.put("lineId", lineId);
        return jdbcTemplate.query(sql, new MapSqlParameterSource(params), rowMapper);
    }
}
