package wooteco.subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.Line;
import wooteco.subway.station.Station;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) -> new Section(
            resultSet.getLong("id"),
            new Line(resultSet.getLong("line_id")),
            new Station(resultSet.getLong("up_station_id")),
            new Station(resultSet.getLong("down_station_id")),
            resultSet.getInt("distance"));

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Section section) {
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql, section.getLine().getId(),
                section.getUpStation().getId(), section.getDownStation().getId(), section.getDistance());
    }

    public Map<Long, Long> sectionMap(long id) {
        String sql = "SELECT up_station_id, down_station_id FROM SECTION WHERE line_id = ?";

        return jdbcTemplate.query(sql, sectionMapExtractor(), id);
    }

    public void delete(Section section) {
        String sql = "DELETE FROM SECTION WHERE line_id = ? AND up_station_id = ? AND down_station_id = ?";
        jdbcTemplate.update(sql, section.getLine().getId(), section.getUpStation().getId(), section.getDownStation().getId());
    }

    public int distance(Section section) {
        String sql = "SELECT distance FROM SECTION WHERE line_id = ? AND up_station_id = ? AND down_station_id = ?";
        return Objects.requireNonNull(jdbcTemplate.queryForObject(sql, Integer.class,
                section.getLine().getId(), section.getUpStation().getId(), section.getDownStation().getId()));
    }

    public boolean isExistStation(long lineId, long stationId) {
        String sql = "SELECT EXISTS (SELECT * FROM SECTION WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)) AS SUCCESS";
        return Objects.requireNonNull(jdbcTemplate.queryForObject(sql, Boolean.class, lineId, stationId, stationId));
    }

    public List<Long> findDownStation(long lineId, long upStationId) {
        String sql = "SELECT down_station_id FROM SECTION WHERE line_id = ? AND up_station_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, lineId, upStationId);
    }

    public List<Long> findUpStation(long lineId, long downStationId) {
        String sql = "SELECT up_station_id FROM SECTION WHERE line_id = ? AND down_station_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, lineId, downStationId);
    }

    public int count(long lineId) {
        String sql = "SELECT count(*) FROM SECTION WHERE line_id = ?";
        return Objects.requireNonNull(jdbcTemplate.queryForObject(sql, Integer.class, lineId));
    }

    private ResultSetExtractor<Map<Long, Long>> sectionMapExtractor() {
        Map<Long, Long> stationMap = new HashMap<>();
        return (rs) -> {
            while (rs.next()) {
                stationMap.put(
                        rs.getLong("up_station_id"),
                        rs.getLong("down_station_id")
                );
            }
            return stationMap;
        };
    }

    public List<Section> findBeforeSection(Section newSection) {
        String sql = "SELECT * FROM SECTION WHERE up_station_id = ? OR down_station_id = ?";
        return jdbcTemplate.query(sql, sectionRowMapper, newSection.getUpStation().getId(), newSection.getDownStation().getId());
    }

    public List<Section> findSections(long lineId, long stationId) {
        String sql = "SELECT * FROM SECTION WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        return jdbcTemplate.query(sql, sectionRowMapper, lineId, stationId, stationId);
    }

    public boolean isEndStation(Section newSection) {
        String sql = "SELECT EXISTS (SELECT * FROM SECTION WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)) AS SUCCESS";
        return Objects.requireNonNull(jdbcTemplate.queryForObject(
                sql, Boolean.class,
                newSection.getLine().getId(),
                newSection.getDownStation().getId(),
                newSection.getUpStation().getId())
        );
    }

    public boolean isExistReverseSection(Section section) {
        String sql = "SELECT EXISTS (SELECT * FROM SECTION WHERE line_id = ? AND (up_station_id = ? AND down_station_id = ?)) AS SUCCESS";
        return Objects.requireNonNull(jdbcTemplate.queryForObject(
                sql, Boolean.class,
                section.getLine().getId(),
                section.getDownStation().getId(),
                section.getUpStation().getId()
        ));

    }

    public boolean isExistSection(Section section) {
        String sql = "SELECT EXISTS (SELECT * FROM SECTION WHERE line_id = ? AND (up_station_id = ? AND down_station_id = ?)) AS SUCCESS";
        return Objects.requireNonNull(jdbcTemplate.queryForObject(
                sql, Boolean.class,
                section.getLine().getId(),
                section.getUpStation().getId(),
                section.getDownStation().getId()
        ));
    }
}
