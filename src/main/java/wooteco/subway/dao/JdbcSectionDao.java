package wooteco.subway.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionEntity;

@Repository
public class JdbcSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Long lineId, Section section) {
        String sql = "INSERT INTO SECTION(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(sql, lineId, section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    @Override
    public List<SectionEntity> findByLine(Long lineId) {
        String sql = "SELECT * FROM SECTION WHERE line_id = ?";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new SectionEntity(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance")
        ), lineId);
    }

    @Override
    public void update(Long lineId, Section section) {
        String sql = "UPDATE SECTION SET up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ? AND line_id = ?";
        jdbcTemplate.update(sql, section.getUpStationId(), section.getDownStationId(), section.getDistance(),
            section.getId(), lineId);
    }

    @Override
    public void deleteAll(Long lineId) {
        String sql = "DELETE FROM SECTION WHERE line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }

    @Override
    public void delete(Long lineId, Section section) {
        String sql = "DELETE FROM SECTION WHERE line_id = ? AND id = ?";
        jdbcTemplate.update(sql, lineId, section.getId());
    }

    @Override
    public boolean existSectionUsingStation(Long stationId) {
        String sql = "SELECT EXISTS (SELECT * FROM SECTION WHERE up_station_id = ? OR down_station_id = ?) AS SUCCESS";
        return jdbcTemplate.queryForObject(sql, Boolean.class, stationId, stationId);
    }
}
