package wooteco.subway.section;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import wooteco.subway.station.Station;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Section section) {
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[] {"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public long findStartStationIdByLineId(long id) {
        String sql = "SELECT UP_STATION_ID FROM SECTION WHERE LINE_ID = ? AND UP_STATION_ID NOT IN (SELECT DOWN_STATION_ID FROM SECTION WHERE LINE_ID = ?)";
        return Objects.requireNonNull(jdbcTemplate.queryForObject(sql, Long.class, id, id));
    }

    public long findEndStationIdByLineId(long id) {
        String sql = "SELECT DOWN_STATION_ID FROM SECTION WHERE LINE_ID = ? AND DOWN_STATION_ID NOT IN (SELECT UP_STATION_ID FROM SECTION WHERE LINE_ID = ?)";
        return Objects.requireNonNull(jdbcTemplate.queryForObject(sql, Long.class, id, id));
    }

    public Map<Long, Long> findSectionsByLineId(long id) {
        String sql = "SELECT UP_STATION_ID, DOWN_STATION_ID FROM SECTION WHERE LINE_ID = ?";
        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, id);
        Map<Long, Long> sections = new HashMap<>();

        for(Map<String, Object> result : resultList) {
            sections.put((Long)result.get("UP_STATION_ID"), (Long)result.get("DOWN_STATION_ID"));
        }
        return sections;
    }

    public Optional<Section> findSectionBySameUpStation(long lineId, Station upStation) {
        String sql = "SELECT * FROM SECTION WHERE LINE_ID = ? AND UP_STATION_ID = ?";
        List<Section> result = jdbcTemplate.query(sql, sectionRowMapper, lineId, upStation.getId());
        return result.stream().findAny();
    }

    public Optional<Section> findSectionBySameDownStation(long lineId, Station downStation) {
        String sql = "SELECT * FROM SECTION WHERE LINE_ID = ? AND DOWN_STATION_ID = ?";
        List<Section> result = jdbcTemplate.query(sql, sectionRowMapper, lineId, downStation.getId());
        return result.stream().findAny();
    }

    public int updateDownStation(Section section, Station upStation) {
        String sql = "UPDATE SECTION set DOWN_STATION_ID = ? WHERE LINE_ID = ? AND DOWN_STATION_ID = ?";
        return jdbcTemplate.update(sql, upStation.getId(), section.getLineId(), section.getDownStationId());
    }

    public int updateUpStation(Section section, Station downStation) {
        String sql = "UPDATE SECTION set UP_STATION_ID = ? WHERE LINE_ID = ? AND UP_STATION_ID = ?";
        return jdbcTemplate.update(sql, downStation.getId(), section.getLineId(), section.getUpStationId());
    }

    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) -> new Section(
        resultSet.getLong("LINE_ID"),
        resultSet.getLong("UP_STATION_ID"),
        resultSet.getLong("DOWN_STATION_ID"),
        resultSet.getInt("DISTANCE")
    );

    public int deleteSection(Section section) {
        String sql = "DELETE FROM SECTION where UP_STATION_ID = ?";
        return jdbcTemplate.update(sql, section.getUpStationId());
    }
}
