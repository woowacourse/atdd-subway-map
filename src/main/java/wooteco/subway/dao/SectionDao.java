package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public Long insert(Section section) {
        Map<String, Object> params = new HashMap<>(4);
        params.put("line_id", section.getLineId());
        params.put("up_station_id", section.getUpStationId());
        params.put("down_station_id", section.getDownStationId());
        params.put("distance", section.getDistance());
        return simpleJdbcInsert.executeAndReturnKey(params).longValue();
    }

    public List<Section> findAllByLineId(Long lineId) {
        String query = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(query, SECTION_MAPPER, lineId);
    }

    public boolean isIncludeAllEndStations(Section section) {
        String query = "SELECT count(id) FROM section WHERE line_id = ? AND up_station_id = ? AND down_station_id = ?";
        final int cnt = jdbcTemplate.queryForObject(
                query,
                Integer.class,
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId()
        );
        return cnt > 0;
    }

    public Optional<Section> findOneIfInclude(Section section) {
        String query = "SELECT * FROM section WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        return jdbcTemplate.query(
                query,
                SECTION_MAPPER,
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId()).stream()
                .findAny();
    }

    public void update(Section section) {
        String query = "UPDATE section SET up_station_id = ?, down_station_id = ?, distance = ? " +
                "WHERE line_id = ? AND id = ?";
        jdbcTemplate.update(query, section.getUpStationId(), section.getDownStationId(),
                section.getDistance(), section.getLineId(), section.getId());
    }

    public Optional<Section> findOneIfIncludeConversed(Section section) {
        String query = "SELECT * FROM section WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        return jdbcTemplate.query(
                query,
                SECTION_MAPPER,
                section.getLineId(),
                section.getDownStationId(),
                section.getUpStationId()).stream()
                .findAny();
    }

    public int countsByLineId(Long lineId) {
        String query = "SELECT count(id) FROM section WHERE line_id = ?";
        return jdbcTemplate.queryForObject(
                query,
                Integer.class,
                lineId
        );
    }

    public List<Section> findAllSectionsIncludeStationId(Long lineId, Long stationId) {
        String query = "SELECT * FROM section WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        return jdbcTemplate.query(
                query,
                SECTION_MAPPER,
                lineId,
                stationId,
                stationId);
    }

    public void delete(Section section) {
        String query = "DELETE FROM section WHERE id = ?";
        jdbcTemplate.update(query, section.getId());
    }

    public void deleteAllByLineId(Long lineId) {
        String query = "DELETE FROM section WHERE line_id = ?";
        jdbcTemplate.update(query, lineId);
    }
}
