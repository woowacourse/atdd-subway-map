package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(final Long lineNumber, final Long upStationId, final Long downStationId, final int distance) {
        final String sql = "insert into section(line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql, lineNumber, upStationId, downStationId, distance);
    }

    public void deleteById(Long lineNumber, Long stationId) {
        final String sql = "delete from section where line_id=? and (up_station_id=? or down_station_id=?)";
        jdbcTemplate.update(sql, lineNumber, stationId, stationId);
    }
}
