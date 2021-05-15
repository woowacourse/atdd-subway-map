package wooteco.subway.domain.section;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.web.exception.SubwayHttpException;

@Repository
public class SectionDao {

    private static final String ID = "id";
    private static final String LINE_ID = "line_id";
    private static final String UP_STATION_ID = "up_station_id";
    private static final String DOWN_STATION_ID = "down_station_id";
    private static final String DISTANCE = "distance";

    private static final RowMapper<Section> SECTION_ROW_MAPPER = (rs, rowNum) ->
            new Section(
                    rs.getLong(ID),
                    rs.getLong(LINE_ID),
                    rs.getLong(UP_STATION_ID),
                    rs.getLong(DOWN_STATION_ID),
                    rs.getInt(DISTANCE)
            );

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public SectionDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public Long save(Section section) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("line_id", section.getLineId());
        params.addValue("up_station_id", section.getUpStationId());
        params.addValue("down_station_id", section.getDownStationId());
        params.addValue("distance", section.getDistance());

        try {
            return simpleJdbcInsert.executeAndReturnKey(params).longValue();
        } catch (Exception e) {
            throw new SubwayHttpException("중복된 역 이름입니다");
        }
    }

    public List<Section> findSectionsByLineId(Long lineId) {
        final String sql = "SELECT id, line_id, up_station_id, down_station_id, distance "
                + "FROM section "
                + "WHERE line_id = :lineId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("lineId", lineId);

        return namedParameterJdbcTemplate.query(sql, params, SECTION_ROW_MAPPER);
    }

    public Long countSection(Long lineId, Long stationId) {
        final String sql = "SELECT COUNT(*) "
                + "FROM section "
                + "WHERE line_id = :lineId "
                + "AND "
                + "(up_station_id = :stationId OR down_station_id = :stationId)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("lineId", lineId);
        params.addValue("stationId", stationId);

        return namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
    }

    public void delete(Long id) {
        final String sql = "DELETE FROM section WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        namedParameterJdbcTemplate.update(sql, params);
    }

    public Optional<Section> priorSection(Long lineId, Long upStationId, Long downStationId) {
        final String sql = "SELECT id, line_id, up_station_id, down_station_id, distance "
                + "FROM section "
                + "WHERE line_id = :lineId "
                + "AND "
                + "(up_station_id = :upStationId OR down_station_id = :downStationId)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("lineId", lineId);
        params.addValue("upStationId", upStationId);
        params.addValue("downStationId", downStationId);

        List<Section> sections = namedParameterJdbcTemplate
                .query(sql, params, SECTION_ROW_MAPPER);

        if (sections.isEmpty()) {
            return Optional.empty();
        }
        if (sections.size() >= 2) {
            throw new IllegalArgumentException("조건에 해당하는 구간이 2개 이상입니다");
        }
        return Optional.of(sections.get(0));
    }

    public List<Section> countSectionByStationId(Long lineId, Long stationId) {
        final String sql = "SELECT id, line_id, up_station_id, down_station_id, distance "
                + "FROM section "
                + "WHERE "
                + "line_id = :lineId "
                + "AND "
                + "(up_station_id = :stationId OR down_station_id = :stationId)";

        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("lineId", lineId);
        params.addValue("stationId", stationId);

        return namedParameterJdbcTemplate.query(sql, params, SECTION_ROW_MAPPER);
    }

    public void deleteSectionByStationId(Long lineId, Long stationId) {
        final String sql = "DELETE FROM section "
                + "WHERE "
                + "line_id = :lineId "
                + "AND "
                + "(up_station_id = :stationId OR down_station_id = :stationId)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("lineId", lineId);
        params.addValue("stationId", stationId);

        namedParameterJdbcTemplate.update(sql, params);
    }
}
