package wooteco.subway.section.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class JdbcSectionDao implements SectionDao {
    private static final String CREATE = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) " +
            "VALUES (?, ?, ?, ?)";
    private static final String READ = "select id, " +
            "(select name from station where station.id = section.up_station_id) as upStationName, " +
            "up_station_id as upStationId,  " +
            "(select name from station where station.id = section.down_station_id) as downStationName, " +
            "down_station_id as downStationId, " +
            "distance " +
            "from section where line_id = ?;";
    private static final String UPDATE = "UPDATE section SET up_station_id = ?, down_station_id = ?, distance = ? " +
            "WHERE line_id = ?";
    private static final String READ_BY_ID_AND_STATION = "select id, " +
            "(select name from station where station.id = section.up_station_id) as upStationName, " +
            "up_station_id as upStationId,  " +
            "(select name from station where station.id = section.down_station_id) as downStationName, " +
            "down_station_id as downStationId, " +
            "distance " +
            "from section where line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
    private static final String DELETE = "DELETE FROM section WHERE line_id = ? AND up_station_id = ? AND down_station_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Section create(Section section, Long lineId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(CREATE, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, section.getUpStation().getId());
            ps.setLong(3, section.getDownStation().getId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        return Section.create(keyHolder.getKey().longValue(), section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    @Override
    public Sections findAllByLineId(Long lineId) {
        List<Section> sections = jdbcTemplate.query(READ, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String upStationName = rs.getString("upStationName");
            Long upStationId = rs.getLong("upStationId");
            Station upStation = Station.create(upStationId, upStationName);

            String downStationName = rs.getString("downStationName");
            Long downStationId = rs.getLong("downStationId");
            Station downStation = Station.create(downStationId, downStationName);

            int distance = rs.getInt("distance");
            return Section.create(id, upStation, downStation, distance);
        }, lineId);

        return Sections.create(sections);
    }

    @Override
    public void saveModified(Section updateSection, Long lineId) {
        jdbcTemplate.update("UPDATE section SET up_station_id = ?, down_station_id = ?, distance = ? " +
                "WHERE line_id = ?", updateSection.getUpStation().getId(), updateSection.getDownStation().getId(), updateSection.getDistance(), lineId);

    }

    @Override
    public List<Section> findAdjacentByStationId(Long lineId, Long stationId) {
        List<Section> sections = this.jdbcTemplate.query(READ_BY_ID_AND_STATION, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String upStationName = rs.getString("upStationName");
            Long upStationId = rs.getLong("upStationId");
            Station upStation = Station.create(upStationId, upStationName);

            String downStationName = rs.getString("downStationName");
            Long downStationId = rs.getLong("downStationId");
            Station downStation = Station.create(downStationId, downStationName);

            int distance = rs.getInt("distance");
            return Section.create(id, upStation, downStation, distance);
        }, lineId, stationId, stationId);

        return sections;
    }

    @Override
    public void removeSections(Long lineId, List<Section> sections) {
        for (Section section : sections) {
            Long upStationId = section.getUpStation().getId();
            Long downStationId = section.getDownStation().getId();
            jdbcTemplate.update(DELETE, lineId, upStationId, downStationId);
        }
    }

    @Override
    public void insertSection(Section affectedSection, Long lineId) {
        jdbcTemplate.update(CREATE, lineId,
                affectedSection.getUpStation().getId(),
                affectedSection.getDownStation().getId(),
                affectedSection.getDistance());
    }
}
