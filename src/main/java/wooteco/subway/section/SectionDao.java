package wooteco.subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Long lineId, Section section) {
        String sql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql, lineId, section.upStation().getId(), section.downStation().getId(), section.distance().intValue());
    }
}
