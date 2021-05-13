package wooteco.subway.section.dao;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.api.dto.SectionDto;
import wooteco.subway.section.model.Section;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Section section) { //todo: DTO 대신 Domain 받는 구조로 변경!
        String sql = "INSERT INTO `section` (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, section.getLineId(), section.getUpStationId(),
            section.getDownStationId(), section.getDistance());
    }

    public void saveAll(List<Section> sections) {
        String sql = "INSERT INTO `section` (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        List<Object[]> params = sections.stream()
            .map(this::parseToSectionParams)
            .collect(Collectors.toList());
        jdbcTemplate.batchUpdate(sql, params);
    }

    private Object[] parseToSectionParams(Section section) {
        return new Object[]{section.getLineId(), section.getUpStationId(),
            section.getDownStationId(),
            section.getDistance()};
    }

    public List<SectionDto> findSectionsByLineId(Long id) {
        String sql = "SELECT id, line_id, up_station_id, down_station_id, distance " +
            "FROM section WHERE line_id = ?";
        return jdbcTemplate.query(sql, mapperToSectionDto(), id);
    }

    private RowMapper<SectionDto> mapperToSectionDto() {
        return (rs, rowNum) -> {
            Long sectionId = rs.getLong("id");
            Long lineId = rs.getLong("line_id");
            Long upStationId = rs.getLong("up_station_id");
            Long downStationId = rs.getLong("down_station_id");
            int distance = rs.getInt("distance");
            return new SectionDto(sectionId, lineId, upStationId, downStationId, distance);
        };
    }

    public void deleteAllByLineId(Long lineId) {
        String sql = "DELETE FROM section WHERE line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }
}
