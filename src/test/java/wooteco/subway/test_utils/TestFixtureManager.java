package wooteco.subway.test_utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestFixtureManager {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public void saveStations(String... names) {
        String sql = "INSERT INTO station(name) VALUES (?)";
        for (String stationName : names) {
            jdbcTemplate.update(sql, stationName);
        }
    }

    public void saveLine(String name, String color) {
        String sql = "INSERT INTO line(name, color) VALUES (?, ?)";
        jdbcTemplate.update(sql, name, color);
    }

    public void saveSection(Long lineId, Long upStationId, Long downStationId) {
        saveSection(lineId, upStationId, downStationId,10);
    }

    public void saveSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        String sql = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) "
                + "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, lineId, upStationId, downStationId, distance);
    }
}
