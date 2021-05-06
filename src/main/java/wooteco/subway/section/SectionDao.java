package wooteco.subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(long lineId, long upStationId, long downStationId) {
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id) values (?, ?, ?)";
        jdbcTemplate.update(sql, lineId, upStationId, downStationId);
    }
}
