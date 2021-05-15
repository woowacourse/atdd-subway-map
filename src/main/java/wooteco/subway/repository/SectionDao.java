package wooteco.subway.repository;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Section section, Long lindId) {
        String saveQuery = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = (connection) -> {
            PreparedStatement prepareStatement = connection
                .prepareStatement(saveQuery, new String[]{"id"});
            prepareStatement.setLong(1, lindId);
            prepareStatement.setLong(2, section.getUpStationId());
            prepareStatement.setLong(3, section.getDownStationId());
            prepareStatement.setLong(4, section.getDistance());
            return prepareStatement;
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public Sections findByLine(Long lineId) {
        String loadQuery = "SELECT id, up_station_id, down_station_id, " +
            "(SELECT name FROM station WHERE station.id = section.up_station_id) AS upStation, " +
            "(SELECT name FROM station WHERE station.id = section.down_station_id) AS downStation, "
            +
            "distance FROM section WHERE line_id = ?";
        List<Section> sections = jdbcTemplate.query(loadQuery, (rs, rowNum) -> {
            long sectionId = rs.getLong("id");
            Station upStation = new Station(rs.getLong("up_station_id"), rs.getString("upStation"));
            Station downStation = new Station(rs.getLong("down_station_id"),
                rs.getString("downStation"));
            int distance = rs.getInt("distance");
            return new Section(sectionId, upStation, downStation, distance);
        }, lineId);
        return new Sections(sections);
    }

    public void deleteByLineId(Long lineId) {
        String deleteQuery = "DELETE * FROM SECTION WHERE line_id = ?";
        jdbcTemplate.update(deleteQuery, lineId);
    }

    public void deleteByStation(Long lindId, Long stationId) {
        String deleteQuery = "DELETE FROM SECTION "
            + "WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        jdbcTemplate.update(deleteQuery, lindId, stationId, stationId);
    }

    public List<Section> findByStation(Long lindId, Long stationId) {
        String findQuery = "SELECT id, up_station_id, down_station_id, distance FROM SECTION "
            + "WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        return jdbcTemplate.query(findQuery, (rs, ronNum) -> {
            long sectionId = rs.getLong("id");
            Station upStation = new Station(rs.getLong("up_station_id"), rs.getString("upStation"));
            Station downStation = new Station(rs.getLong("down_station_id"),
                rs.getString("downStation"));
            int distance = rs.getInt("distance");
            return new Section(sectionId, upStation, downStation, distance);
        });
    }

    public void update(Section section, Long lineId) {
        String updateQuery = "UPDATE section "
            + "SET up_station_id = ?, down_station_id = ?, distance = ? "
            + "WHERE id = ? AND line_id = ?";
        jdbcTemplate.update(updateQuery, section.getUpStationId(), section.getDownStationId(),
            section.getDistance(), section.getId(), lineId);
    }
}
