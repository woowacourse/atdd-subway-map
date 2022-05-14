package wooteco.subway.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DataNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class SectionDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<Section> rowMapper = (rs, row) -> {
        final long id = rs.getLong("id");
        final long lineId = rs.getLong("line_id");
        final long upStationId = rs.getLong("up_station_id");
        final long downStationId = rs.getLong("down_station_id");
        final int distance = rs.getInt("distance");
        return new Section(id, new Station(upStationId, rs.getString("up_name")), new Station(downStationId, rs.getString("down_name")), distance, lineId);
    };

    public SectionDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Section save(final Section section) {
        final String sql = "INSERT INTO SECTION(up_station_id, down_station_id, distance, line_id) " +
                "VALUES (:upStationId, :downStationId, :distance, :lineId)";

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

    public List<Section> findAll() {

        final String sql = "SELECT SC.id, SC.up_station_id, SC.down_station_id, SC.distance, SC.line_id, " +
                "S1.name as up_name, S2.name as down_name " +
                "FROM SECTION SC " +
                "JOIN STATION S1 ON SC.up_station_id=S1.id " +
                "JOIN STATION S2 ON SC.down_station_id=S2.id";

        return namedParameterJdbcTemplate.query(sql, rowMapper);
    }

    public List<Section> findAllByLineId(final long lineId) {
        final String sql = "SELECT SC.id, SC.up_station_id, SC.down_station_id, SC.distance, SC.line_id, " +
                "S1.name as up_name, S2.name as down_name " +
                "FROM SECTION SC " +
                "JOIN STATION S1 ON SC.up_station_id=S1.id " +
                "JOIN STATION S2 ON SC.down_station_id=S2.id " +
                "WHERE SC.line_id=:lineId";

        final Map<String, Object> params = new HashMap<>();
        params.put("lineId", lineId);

        return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource(params), rowMapper);
    }

    public void update(final Long id, final Section newSection) {
        final String sql = "UPDATE SECTION SET up_station_id=:upStationId, down_station_id=:downStationId, distance=:distance WHERE id=:id";

        final Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("upStationId", newSection.getUpStation().getId());
        params.put("downStationId", newSection.getDownStation().getId());
        params.put("distance", newSection.getDistance());

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }

    public void deleteAll(final List<Section> sections) {
        final String sql = "DELETE FROM SECTION WHERE id = :id";
        namedParameterJdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(sections));
    }
}
