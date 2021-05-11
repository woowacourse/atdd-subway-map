package wooteco.subway.section.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.Section;
import wooteco.subway.station.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class JdbcSectionDao {
    private JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationMapper = (rs, rowNum) -> new Station (
            rs.getLong("id"),
            rs.getString("name")
    );
    private final RowMapper<Section> sectionMapper = (rs, rowNum) -> new Section (
            rs.getLong("id"),
            rs.getLong("line_id"),
            rs.getLong("up_station_id"),
            rs.getLong("down_station_id"),
            rs.getInt("distance")
    );

    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Long lineId, Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(con -> {
            PreparedStatement pstmt = con.prepareStatement(query, new String[]{"id"});
            pstmt.setLong(1, lineId);
            pstmt.setLong(2, section.getUpStationId());
            pstmt.setLong(3, section.getDownStationId());
            pstmt.setInt(4, section.getDistance());
            return pstmt;
        }, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Section(id, lineId, section);
    }

    public List<Station> findStationsBy(Long stationId, Long downStationId) {
        String query = "SELECT * FROM station WHERE id in (?, ?)";
        return jdbcTemplate.query(query, stationMapper, stationId, downStationId);
    }

    public List<Section> findAllByLineId(Long lineId) {
        String query = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(query, sectionMapper, lineId);
    }

    public Section findByUpStationId(Long lineId, Long upStationId) {
        String query = "SELECT * FROM section WHERE up_station_id = ? AND line_id = ?";
        return jdbcTemplate.query(query, sectionMapper, upStationId, lineId).get(0);
    }

    public Section findByDownStationId(Long lineId, Long downStationId) {
        String query = "SELECT * FROM section WHERE down_station_id = ? AND line_id = ?";
        return jdbcTemplate.query(query, sectionMapper, downStationId, lineId).get(0);
    }

    public Section appendToUp(Long lineId, Section newSection, int changedDistance) {
        updateAndAppendToUp(lineId, newSection, changedDistance);
        return save(lineId, newSection);
    }

    public Section appendBeforeDown(Long lineId, Section newSection, int changedDistance) {
        updateAndAppendBeforeDown(newSection, changedDistance);
        return save(lineId, newSection);
    }

    private void updateAndAppendToUp(Long lineId, Section newSection, int changedDistance) {
        String query = "UPDATE section SET up_station_id = ?, distance = ? WHERE up_station_id = ? AND line_id = ?";
        jdbcTemplate.update(query, newSection.getDownStationId(), changedDistance, newSection.getUpStationId(), lineId);
    }

    private void updateAndAppendBeforeDown(Section newSection, int changedDistance) {
        String query = "UPDATE section SET down_station_id = ?, distance = ? WHERE down_station_id = ?";
        jdbcTemplate.update(query, newSection.getUpStationId(), changedDistance, newSection.getDownStationId());
    }

    public void deleteFirstSection(Long lineId, Long stationId) {
        String query = "DELETE FROM section WHERE line_id = ? AND up_station_id = ?";
        jdbcTemplate.update(query, lineId, stationId);
    }

    public void deleteLastSection(Long lineId, Long stationId) {
        String query = "DELETE FROM section WHERE line_id = ? AND down_station_id = ?";
        jdbcTemplate.update(query, lineId, stationId);
    }

    public void deleteSections(Section before, Section after) {
        String query = "DELETE FROM section WHERE id = ? AND id = ?";
        jdbcTemplate.update(query, before.getId(), after.getId());
    }
}
