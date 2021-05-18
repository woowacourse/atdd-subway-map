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
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(final Section section) {
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

        return new Section(
                Objects.requireNonNull(keyHolder.getKey()).longValue(),
                section.getLineId(),
                section.getUpStation(),
                section.getDownStation(),
                section.getDistance()
        );
    }

    public void deleteByStationId(final Long lineId, final Long stationId) {
        String query = "DELETE FROM section WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        jdbcTemplate.update(query, lineId, stationId, stationId);
    }

    public Sections findAllByLineId(final Long lineId) {
        String query = "SELECT section.id AS sectionId, up_station_id, down_station_id, distance, up.name AS upName, down.name AS downName " +
                "FROM section LEFT JOIN station AS up ON section.up_station_id = up.id " +
                "LEFT JOIN station AS down ON section.down_station_id = down.id " +
                "WHERE line_id = ?";

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
                lineId);

        return new Sections(sections);
    }

    public void update(final Section section) {
        String query = "UPDATE section SET up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";
        jdbcTemplate.update(query, section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getId());
    }
}
