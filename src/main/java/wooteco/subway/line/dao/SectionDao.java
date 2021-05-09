package wooteco.subway.line.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.domain.Station;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@Repository
public class SectionDao {

    private JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Long lineId, Long upStationId, Long downStationId, int distance) {
        String sql = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(sql, lineId, upStationId, downStationId, distance);
    }

    public Map<Station, Station> findSectionById(Long lineId) {
        String query = "select up_station_id, s1.name, down_station_id, s2.name " +
                "from section " +
                "INNER JOIN station as s1 " +
                "INNER JOIN station as s2 " +
                "where section.line_id = ? AND section.up_station_id = s1.id AND section.down_station_id = s2.id";
        return jdbcTemplate.query(query, stationRowMapper(), lineId);
    }

    private ResultSetExtractor<Map<Station, Station>> stationRowMapper() {
        return (ResultSet rs) -> {
            Map<Station, Station> stationMap = new HashMap<>();
            while (rs.next()) {
                stationMap.put(
                        new Station(rs.getLong(1), rs.getString(2)),
                        new Station(rs.getLong(3), rs.getString(4)));
            }
            return stationMap;
        };
    }
}
