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
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Section save(Section section, Long lineId) {
        String sql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, section.getUpStation().getId());
            ps.setLong(3, section.getDownStation().getId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        return Section.create(keyHolder.getKey().longValue(), section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    @Override
    public Sections findSectionsByLineId(Long lineId) {
        String sql = "select id, (select name from station where station.id = section.up_station_id) as upStationName, " +
                "up_station_id as upStationId,  (select name from station where station.id = section.down_station_id) as downStationName, " +
                "down_station_id as downStationId, distance from section where line_id = ?;";
        List<Section> sections = this.jdbcTemplate.query(sql, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String upStationName = rs.getString("upStationName");
            Long upStationId = rs.getLong("upStationId");
            Station upStation = Station.from(upStationId, upStationName);

            String downStationName = rs.getString("downStationName");
            Long downStationId = rs.getLong("downStationId");
            Station downStation = Station.from(downStationId, downStationName);

            int distance = rs.getInt("distance");
            return Section.create(id, upStation, downStation, distance);
        }, lineId);

        Sections from = Sections.from(sections);
        return from;
    }


    @Override
    public Section saveAffectedSections(Section section, Optional<Section> affectedSection, Long lineId) {
        if (affectedSection.isPresent()) {
            Section updateSection = affectedSection.get();
            String update = "UPDATE section SET up_station_id = ?, down_station_id = ?, distance = ? WHERE line_id = ?";
            jdbcTemplate.update(update, updateSection.getUpStation().getId(), updateSection.getDownStation().getId(), updateSection.getDistance(), lineId);
        }
        return save(section, lineId);
   }

    @Override
    public List<Section> findSectionContainsStationId(Long lineId, Long stationId) {
        String sql = "select id, (select name from station where station.id = section.up_station_id) as upStationName, " +
                "up_station_id as upStationId,  (select name from station where station.id = section.down_station_id) as downStationName, " +
                "down_station_id as downStationId, distance from section where line_id = ? AND (up_station_id = ? OR down_station_id = ?)";

        List<Section> sections = this.jdbcTemplate.query(sql, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String upStationName = rs.getString("upStationName");
            Long upStationId = rs.getLong("upStationId");
            Station upStation = Station.from(upStationId, upStationName);

            String downStationName = rs.getString("downStationName");
            Long downStationId = rs.getLong("downStationId");
            Station downStation = Station.from(downStationId, downStationName);

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
            String sql = "DELETE FROM section WHERE line_id = ? AND up_station_id = ? AND down_station_id = ?";
            jdbcTemplate.update(sql, lineId, upStationId, downStationId);
        }
    }

    @Override
    public void insertSection(Section affectedSection, Long lineId) {
        String sql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, lineId,
                affectedSection.getUpStation().getId(),
                affectedSection.getDownStation().getId(),
                affectedSection.getDistance());
    }
}
