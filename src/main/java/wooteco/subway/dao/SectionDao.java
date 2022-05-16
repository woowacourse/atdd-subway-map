package wooteco.subway.dao;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.entity.SectionEntity;

@Repository
public class SectionDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public SectionDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public SectionEntity save(SectionEntity sectionEntity) {
        String sql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values (:lineId, :upStationId, :downStationId, :distance)";

        Map<String, Object> params = new HashMap<>();
        params.put("lineId", sectionEntity.getLineId());
        params.put("upStationId", sectionEntity.getUpStationId());
        params.put("downStationId", sectionEntity.getDownStationId());
        params.put("distance", sectionEntity.getDistance());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        return new SectionEntity.Builder(sectionEntity.getLineId(), sectionEntity.getUpStationId(),
                sectionEntity.getDownStationId(), sectionEntity.getDistance())
                .id(Objects.requireNonNull(keyHolder.getKey()).longValue())
                .build();
    }

    public List<SectionEntity> findAllByLineId(Long lineId) {
        String sql = "select * from SECTION where line_id = :lineId";

        Map<String, Object> params = new HashMap<>();
        params.put("lineId", lineId);

        return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource(params),
                (rs, rowNum) -> new SectionEntity.Builder(
                        rs.getLong("line_id"),
                        rs.getLong("up_station_id"),
                        rs.getLong("down_station_id"),
                        rs.getInt("distance"))
                        .id(rs.getLong("id"))
                        .build());
    }

    public int deleteById(Long id) {
        String sql = "delete from SECTION where id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }
}
