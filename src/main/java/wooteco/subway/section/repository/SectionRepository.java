package wooteco.subway.section.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.domain.Section;
import wooteco.subway.station.domain.Station;

import java.util.HashMap;
import java.util.Map;

@Repository
public class SectionRepository {
    private final JdbcTemplate jdbcTemplate;

    public SectionRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean doesSectionExist(final Section section) {
        String query = "SELECT EXISTS(SELECT * FROM section WHERE line_id = ? AND up_station_id = ? AND down_station_id = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, section.getLineId(), section.getUpStationId(), section.getDownStationId());
    }

    public boolean doesStationNotExist(final Long lineId, final Long stationId) {
        String query = "SELECT NOT EXISTS(SELECT * FROM section WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?))";
        return jdbcTemplate.queryForObject(query, Boolean.class, lineId, stationId, stationId);
    }

    public void save(final Section section) {
        String query = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(query, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public boolean isUnableToDelete(final Long lineId) {
        String query = "SELECT COUNT(*) FROM section WHERE line_id = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, lineId);

        return count == 1;
    }

    public boolean isEndStation(final Long lineId, final Long stationId) {
        String query = "SELECT COUNT(*) FROM section WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, lineId, stationId, stationId);

        return count != 2;
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        String query = "DELETE FROM section WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        jdbcTemplate.update(query, lineId, stationId, stationId);
    }

    public int getNewSectionDistance(final Long lineId, final Long stationId) {
        String query = "SELECT SUM(distance) FROM section WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        return jdbcTemplate.queryForObject(query, Integer.class, lineId, stationId, stationId);
    }

    public long getNewUpStationId(final Long lineId, final Long stationId) {
        String query = "SELECT up_station_id FROM section WHERE line_id = ? AND down_station_id = ?";
        return jdbcTemplate.queryForObject(query, Long.class, lineId, stationId);
    }

    public long getNewDownStationId(final Long lineId, final Long stationId) {
        String query = "SELECT down_station_id FROM section WHERE line_id = ? AND up_station_id = ?";
        return jdbcTemplate.queryForObject(query, Long.class, lineId, stationId);
    }

    public Map<Station, Station> getAllUpAndDownStations(final Long lineId) {
        String query = "SELECT up_station_id, up.name AS upName, down_station_id, down.name AS downName " +
                "FROM (SELECT section.id AS id, up_station_id, name FROM section JOIN station ON section.up_station_id = station.id WHERE section.line_id = ?) up " +
                "JOIN (SELECT section.id AS id, down_station_id, name FROM section JOIN station ON section.down_station_id = station.id WHERE section.line_id = ?) down " +
                "ON up.id = down.id";

        Map<Station, Station> sections = new HashMap<>();
        jdbcTemplate.query(
                query,
                (resultSet, rowNum) -> {
                    Station upStation = new Station(
                            resultSet.getLong("up_station_id"),
                            resultSet.getString("upName")
                    );

                    Station downStation = new Station(
                            resultSet.getLong("down_station_id"),
                            resultSet.getString("downName")
                    );
                    sections.put(upStation, downStation);
                    return null;
                },
                lineId,
                lineId);
        return sections;
    }
}
