package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import wooteco.subway.dto.section.SectionRequest;

@Component
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(long lineId, SectionRequest sectionRequest) {
        var sql = "INSERT INTO section (up_station_id, down_station_id, distance, line_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance(),
                lineId
        );
    }
}
