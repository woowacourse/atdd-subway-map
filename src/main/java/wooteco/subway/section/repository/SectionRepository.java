package wooteco.subway.section.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.domain.Section;

@Repository
public class SectionRepository {
    private final JdbcTemplate jdbcTemplate;

    public SectionRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean doesSectionExist(final Section section) {
        String query = "SELECT EXISTS(SELECT * FROM Line WHERE line_id = ?, up_station_id = ?, down_station_id = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, section.getLineId(), section.getUpStationId(), section.getDownStationId());
    }

    public void save(final Section section) {
        String query = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(query, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }
}
