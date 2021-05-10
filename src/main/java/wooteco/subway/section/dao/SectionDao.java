package wooteco.subway.section.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.api.dto.LineRequest;
import wooteco.subway.section.model.Section;

import java.util.List;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Section> mapperSection = (rs, rowNum) -> {
        Long id = rs.getLong("id");
        Long lineId = rs.getLong("line_id");
        Long upStationId = rs.getLong("up_station_id");
        Long downStationId = rs.getLong("down_station_id");
        int distance = rs.getInt("distance");
        return new Section(id, lineId, upStationId, downStationId, distance);
    };

    public void save(long createdId, LineRequest lineRequest) {
        String sql = "INSERT INTO `section` (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, createdId, lineRequest.getUpStationId(),
                lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    public List<Section> findSectionsByLineId(Long id) {
        String sql = "SELECT id, line_id, up_station_id, down_station_id, distance " +
                "FROM section WHERE line_id = ?";
        return jdbcTemplate.query(sql, mapperSection, id);
    }
}
