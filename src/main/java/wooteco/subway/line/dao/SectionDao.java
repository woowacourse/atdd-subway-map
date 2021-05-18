package wooteco.subway.line.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;
import wooteco.subway.station.domain.Station;

import java.util.List;

@Repository
public class SectionDao {

    private JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Long lineId, Long upStationId, Long downStationId, int distance) {
        String sql = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(sql, lineId, upStationId, downStationId, distance);
    }

    public void save(Section section) {
        String sql = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                section.getLine().getId(),
                section.getUpStation().getId(),
                section.getDownStation().getId(),
                section.getDistance());
    }

    @Transactional(readOnly = true)
    public List<Section> findSectionBylineId(Long lineId) {
        String query = "SELECT up_station_id, s1.name, down_station_id, s2.name, distance " +
                "FROM section " +
                "INNER JOIN station as s1 " +
                "INNER JOIN station as s2 " +
                "WHERE section.line_id = ? AND section.up_station_id = s1.id AND section.down_station_id = s2.id";
        return jdbcTemplate.query(query, sectionRowMapper(lineId), lineId);
    }

    private RowMapper<Section> sectionRowMapper(Long lineId) {
        return (rs, rowNum) -> {
            Station upStation = new Station(rs.getLong(1), rs.getString(2));
            Station downStation = new Station(rs.getLong(3), rs.getString(4));
            return new Section(new Line(lineId), upStation, downStation, rs.getInt(5));
        };
    }

    public void updateUpStation(Long lineId, Long upStationId, Long newUpStationId, int distance) {
        String query = "UPDATE section SET up_station_id = ?, distance = ? " +
                "WHERE line_id = ? " +
                "AND up_station_id = ?";
        jdbcTemplate.update(query, newUpStationId, distance, lineId, upStationId);
    }

    public void updateDownStation(Long lineId, Long downStationId, Long newDownStationId, int distance) {
        String query = "UPDATE section SET down_station_id = ?, distance = ? " +
                "WHERE line_id = ? " +
                "AND down_station_id = ?";
        jdbcTemplate.update(query, newDownStationId, distance, lineId, downStationId);
    }

    public void deleteByStationId(Long lineId, Long stationId) {
        String query = "DELETE FROM section WHERE line_id = ? AND up_station_id = ? OR down_station_id = ?";
        jdbcTemplate.update(query, lineId, stationId, stationId);
    }

    public void deleteByLineId(Long lineId) {
        String query = "DELETE FROM section WHERE line_id = ?";
        jdbcTemplate.update(query, lineId);
    }

    public int countSectionByStationId(Long id) {
        String query = "SELECT count(*) FROM section WHERE up_station_id = ? OR down_station_id = ?";
        return jdbcTemplate.queryForObject(query, Integer.class, id, id);
    }
}
