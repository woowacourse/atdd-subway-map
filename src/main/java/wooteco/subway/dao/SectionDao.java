package wooteco.subway.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Section;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) -> new Section(
        resultSet.getLong("id"),
        resultSet.getLong("line_id"),
        resultSet.getLong("up_station_id"),
        resultSet.getLong("down_station_id"),
        resultSet.getInt("distance")
    );

    public void save(Section section) {
        final String sql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(sql, section.getLineId(), section.getUpStationId(), section.getDownStationId(),
            section.getDistance());
    }

    public List<Section> findAllByLineId(Long id) {
        final String sql = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(sql, sectionRowMapper, id);
    }

    public void updateByUpStationId(Section section) {
        final String sql = "UPDATE section SET down_station_id = ?, distance = ? WHERE line_id = ? AND up_station_id = ?";
        jdbcTemplate.update(sql, section.getDownStationId(), section.getDistance(), section.getLineId(),
            section.getUpStationId());
    }

    public void updateByDownStationId(Section section) {
        final String sql = "UPDATE section SET up_station_id = ?, distance = ? WHERE line_id = ? AND down_station_id = ?";
        jdbcTemplate.update(sql, section.getUpStationId(), section.getDistance(), section.getLineId(),
            section.getDownStationId());
    }

    public void delete(Long lineId, Long upStationId) {
        final String sql = "DELETE FROM section WHERE line_id = ? AND up_station_id = ?";
        jdbcTemplate.update(sql, lineId, upStationId);
    }

    public void deleteEndStation(Long lineId, Long stationId) {
        final String sql = "DELETE FROM section WHERE line_id = ? AND (down_station_id = ? OR up_station_id = ?)";
        jdbcTemplate.update(sql, lineId, stationId, stationId);
    }
}
