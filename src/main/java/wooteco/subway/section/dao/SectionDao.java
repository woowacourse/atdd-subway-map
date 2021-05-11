package wooteco.subway.section.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.api.dto.LineRequest;
import wooteco.subway.section.api.dto.SectionDto;
import wooteco.subway.section.model.Section;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<SectionDto> mapperSection = (rs, rowNum) -> {
        Long id = rs.getLong("id");
        Long lineId = rs.getLong("line_id");
        Long upStationId = rs.getLong("up_station_id");
        Long downStationId = rs.getLong("down_station_id");
        int distance = rs.getInt("distance");
        return new SectionDto(id, lineId, upStationId, downStationId, distance);
    };

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Long lineId, LineRequest lineRequest) {
        String sql = "INSERT INTO `section` (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, lineId, lineRequest.getUpStationId(),
                lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    public void saveAll(List<Section> sections) {
        String sql = "INSERT INTO `section` (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        List<Object[]> params = sections.stream()
                .map(this::parseToSectionParams)
                .collect(Collectors.toList());
        jdbcTemplate.batchUpdate(sql, params);
    }

    private Object[] parseToSectionParams(Section section) {
        return new Object[]{section.getLineId(), section.getUpStationId(), section.getDownStationId(),
                section.getDistance()};
    }

    public List<SectionDto> findSectionsByLineId(Long id) {
        String sql = "SELECT id, line_id, up_station_id, down_station_id, distance " +
                "FROM section WHERE line_id = ?";
        return jdbcTemplate.query(sql, mapperSection, id);
    }

    public void deleteAllByLineId(Long lineId) {
        String sql = "DELETE FROM section WHERE line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }
}
