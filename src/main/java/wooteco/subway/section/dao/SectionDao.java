package wooteco.subway.section.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.api.dto.LineRequest;
import wooteco.subway.line.model.Line;
import wooteco.subway.section.model.Section;
import wooteco.subway.station.model.Station;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Section> mapperSection = (rs, rowNum) -> {
        Long lineId = rs.getLong("line_id");
        String lineName = rs.getString("line_name");
        String lineColor = rs.getString("line_color");
        Long upStationId = rs.getLong("up_station_id");
        String upStationName = rs.getString("up_station_name");
        Long downStationId = rs.getLong("down_station_id");
        String downStationName = rs.getString("down_station_name");
        int distance = rs.getInt("distance");
        return new Section(new Line(lineId, lineName, lineColor),
                new Station(upStationId, upStationName),
                new Station(downStationId, downStationName),
                distance);
    };

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

//    public void save(Long lineId, LineRequest lineRequest) {
//        String sql = "INSERT INTO `section` (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
//        jdbcTemplate.update(sql, lineId, lineRequest.getUpStationId(),
//                lineRequest.getDownStationId(), lineRequest.getDistance());
//    }

    public void save(Section section) {
        String sql = "INSERT INTO `section` (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
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

    public List<Section> findSectionsByLineId(Long id) {
        String sql = "SELECT line.id AS line_id, line.name AS line_name, line.color AS line_color, \n" +
                "up_station.id AS up_station_id, up_station.name AS up_station_name, \n" +
                "down_station.id AS down_station_id, down_station.name AS down_station_name, \n" +
                "s.distance AS distance \n" +
                "FROM section AS s\n" +
                "LEFT JOIN line AS line ON s.line_id = line.id\n" +
                "LEFT JOIN station AS up_station ON s.up_station_id = up_station.id\n" +
                "LEFT JOIN station AS down_station ON s.down_station_id = down_station.id\n" +
                "WHERE s.line_id = ?";
        return jdbcTemplate.query(sql, mapperSection, id);
    }

    public void deleteAllByLineId(Long lineId) {
        String sql = "DELETE FROM section WHERE line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }
}
