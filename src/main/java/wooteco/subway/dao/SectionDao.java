package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Stations;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class SectionDao {
    private JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long insert(Long lineId, Section section) {
        String query = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        executeSaveQuery(lineId, section, query, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private void executeSaveQuery(Long lineId, Section section, String query, KeyHolder keyHolder) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);
    }

    public void delete(Long lineId, Long stationId) {
        String query = "DELETE FROM section WHERE line_id = ? AND up_station_id = ? OR down_station_id = ?";
        jdbcTemplate.update(query, lineId, stationId, stationId);
    }

    public void updateWhenNewStationDownward(Long lineId, Section section) {
        String query = "UPDATE section SET up_station_id=(?), distance = distance - (?) WHERE line_id = (?) AND up_station_id = (?)";
        jdbcTemplate.update(query, section.getDownStationId(), section.getDistance(), lineId, section.getUpStationId());
    }

    public void updateWhenNewStationUpward(Long lineId, Section section) {
        String query = "UPDATE section SET down_station_id=(?), distance = distance - (?) WHERE line_id = (?) AND down_station_id = (?)";
        jdbcTemplate.update(query, section.getUpStationId(), section.getDistance(), lineId, section.getDownStationId());
    }

    public void deleteBottomSection(Long lineId, Section section) {
        String query = "DELETE FROM section WHERE line_id = ? AND down_station_id = ?";
        jdbcTemplate.update(query, lineId, section.getDownStationId());
    }

    public void deleteTopSection(Long lineId, Section section) {
        String query = "DELETE FROM section WHERE line_id = ? AND up_station_id = ?";
        jdbcTemplate.update(query, lineId, section.getUpStationId());
    }

    public List<Section> selectAll(Long lineId, Stations stations) {
        String query = "SELECT line_id, up_station_id, down_station_id, distance FROM section WHERE line_id = ?";
        List<Section> sections = jdbcTemplate.query(query, (resultSet, rowNum) -> {
            Section section = new Section(
                    resultSet.getLong("line_id"),
                    stations.findStationById(resultSet.getLong("up_station_id")),
                    stations.findStationById(resultSet.getLong("down_station_id")),
                    resultSet.getInt("distance")
            );
            return section;
        }, lineId);
        return sections;
    }
}
