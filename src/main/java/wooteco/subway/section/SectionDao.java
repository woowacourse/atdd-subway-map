package wooteco.subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(long lineId, long upStationId, long downStationId, int distance) {
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql, lineId, upStationId, downStationId, distance);
    }

    public Map<Long, Long> sectionMap(long id) {
        String sql = "SELECT up_station_id, down_station_id FROM SECTION WHERE line_id = ?";

        return jdbcTemplate.query(sql, sectionRowMapper(), id);
    }

    private ResultSetExtractor<Map<Long, Long>> sectionRowMapper() {
        Map<Long, Long> stationMap = new HashMap<>();
        return (rs) -> {
            while(rs.next()) {
                stationMap.put(
                        rs.getLong("up_station_id"),
                        rs.getLong("down_station_id")
                );
            }
            return stationMap;
        };
    }

    public void delete(long lineId, long stationId, long downStationId) {
        String sql = "DELETE FROM SECTION WHERE line_id = ? AND up_station_id = ? AND down_station_id = ?";
        jdbcTemplate.update(sql, lineId, stationId, downStationId);
    }

    public int distance(long lineId, long upStationId, long downStationId) {
        String sql = "SELECT distance FROM SECTION WHERE line_id = ? AND up_station_id = ? AND down_station_id = ?";
        return Objects.requireNonNull(jdbcTemplate.queryForObject(sql, Integer.class, lineId, upStationId, downStationId));
    }

    public boolean isExistStation(long lineId, long stationId) {
        String sql = "SELECT EXISTS (SELECT * FROM SECTION WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)) AS SUCCESS";
        return Objects.requireNonNull(jdbcTemplate.queryForObject(sql, Boolean.class, lineId, stationId, stationId));
    }

    public List<Long> findDownStationIdByUpStationId(long lineId, long upStationId) {
        String sql = "SELECT down_station_id FROM SECTION WHERE line_id = ? AND up_station_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, lineId, upStationId);
    }

    public List<Long> findUpStationIdByDownStationId(long lineId, long downStationId) {
        String sql = "SELECT up_station_id FROM SECTION WHERE line_id = ? AND down_station_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, lineId, downStationId);
    }
}
