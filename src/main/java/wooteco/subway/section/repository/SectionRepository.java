package wooteco.subway.section.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class SectionRepository {
    private final JdbcTemplate jdbcTemplate;

    public SectionRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isInitialSave(final Section section) {
        String query = "SELECT NOT EXISTS(SELECT * FROM section WHERE line_id = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, section.getLineId());
    }

    public boolean doesExistInUpStation(final Long lineId, final Long stationId) {
        String query = "SELECT EXISTS(SELECT * FROM section WHERE line_id = ? AND up_station_id = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, lineId, stationId);
    }

    public boolean doesExistInDownStation(final Long lineId, final Long stationId) {
        String query = "SELECT EXISTS(SELECT * FROM section WHERE line_id = ? AND down_station_id = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, lineId, stationId);
    }

    public boolean doesStationExist(final Long lineId, final Long stationId) {
        String query = "SELECT EXISTS(SELECT * FROM section WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?))";
        return jdbcTemplate.queryForObject(query, Boolean.class, lineId, stationId, stationId);
    }

    public long save(final Section section) {
        String query = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public boolean isUnableToDelete(final Long lineId) {
        String query = "SELECT COUNT(*) FROM section WHERE line_id = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, lineId);

        return count == 1;
    }

    public boolean isEndStation(final Long lineId, final Long stationId) {
        String query = "SELECT COUNT(*) FROM section WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, lineId, stationId, stationId);

        return count == 1;
    }

    public void deleteByStationId(final Long lineId, final Long stationId) {
        String query = "DELETE FROM section WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        jdbcTemplate.update(query, lineId, stationId, stationId);
    }

    public int getNewDistance(final Long lineId, final Long stationId) {
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

    public Sections findAllSections(final Long lineId) {
        String query = "SELECT up.id AS sectionId, up_station_id, up.name AS upName, down_station_id, down.name AS downName, up.distance AS distance " +
                "FROM (SELECT section.id AS id, up_station_id, distance, name FROM section JOIN station ON section.up_station_id = station.id WHERE section.line_id = ?) up " +
                "JOIN (SELECT section.id AS id, down_station_id, distance, name FROM section JOIN station ON section.down_station_id = station.id WHERE section.line_id = ?) down " +
                "ON up.id = down.id";

        List<Section> sections = jdbcTemplate.query(
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
                    return new Section(
                            resultSet.getLong("sectionId"),
                            lineId,
                            upStation,
                            downStation,
                            resultSet.getInt("distance")
                    );
                },
                lineId,
                lineId);

        return new Sections(sections);
    }

    public Section findByBaseStation(final Section section) {
        String query;
        Long lineId = section.getLineId();
        if (doesExistInUpStation(lineId, section.getUpStationId())) {
            query = "SELECT id, line_id, up_station_id, down_station_id, distance FROM section WHERE line_id = ? AND up_station_id = ?";
            return sendQuery(query, lineId, section.getUpStationId());
        }
        query = "SELECT id, line_id, up_station_id, down_station_id, distance FROM section WHERE line_id = ? AND down_station_id = ?";
        return sendQuery(query, lineId, section.getDownStationId());
    }

    private Section sendQuery(final String query, final Long lineId, final Long stationId) {
        return jdbcTemplate.queryForObject(
                query,
                (resultSet, rowNum) -> {
                    Station upStation = new Station(resultSet.getLong("up_station_id"));
                    Station downStation = new Station(resultSet.getLong("down_station_id"));

                    return new Section(
                            resultSet.getLong("id"),
                            resultSet.getLong("line_id"),
                            upStation,
                            downStation,
                            resultSet.getInt("distance"));
                },
                lineId,
                stationId
        );
    }

    public void update(final Section section) {
        String query = "UPDATE section SET up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";
        jdbcTemplate.update(query, section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getId());
    }
}
