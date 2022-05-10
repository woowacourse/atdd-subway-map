package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;

public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(final Long lineId, final Section section) {
        final String sql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql, lineId, section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }
}
