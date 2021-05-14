package wooteco.subway.section.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
public class JdbcSectionDao {
    private final JdbcTemplate jdbcTemplate;

    public Section create(Section section, Long lineId) {
        String createSql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(createSql, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, section.getUpStation().getId());
            ps.setLong(3, section.getDownStation().getId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        return Section.create(keyHolder.getKey().longValue(), section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    public List<SectionTable> findAllByLineId(Long targetLineId) {
        String readSql = "SELECT * FROM section WHERE line_id = ?";

        List<SectionTable> sectionTables = jdbcTemplate.query(readSql, sectionRowMapper(), targetLineId);

        return sectionTables;
    }

    private RowMapper<SectionTable> sectionRowMapper() {
        return (rs, rowNum) -> {
            Long id = rs.getLong("id");
            Long lineId = rs.getLong("line_id");
            Long upStationId = rs.getLong("up_station_id");
            Long downStationId = rs.getLong("down_station_id");
            int distance = rs.getInt("distance");
            return new SectionTable(id, lineId, upStationId, downStationId, distance);
        };
    }

    public void saveModified(Section updateSection, Long lineId) {
        String updateSql = "UPDATE section SET up_station_id = ?, down_station_id = ?, distance = ? WHERE line_id = ?";

        jdbcTemplate.update(updateSql, updateSection.getUpStation().getId(), updateSection.getDownStation().getId(), updateSection.getDistance(), lineId);
    }

    public List<SectionTable> findAdjacentByStationId1111(Long lineId, Long stationId) {
        String findAdjacentSql = "SELECT * FROM section WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        List<SectionTable> sectionTables = this.jdbcTemplate.query(findAdjacentSql, sectionRowMapper(), lineId, stationId, stationId);
        return sectionTables;
    }

    public void removeSections(Long lineId, List<Section> sections) {
        String deleteSql = "DELETE FROM section WHERE line_id = ? AND up_station_id = ? AND down_station_id = ?";

        for (Section section : sections) {
            Long upStationId = section.getUpStation().getId();
            Long downStationId = section.getDownStation().getId();
            jdbcTemplate.update(deleteSql, lineId, upStationId, downStationId);
        }
    }

    public void insertSection(Section affectedSection, Long lineId) {
        String createSql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(createSql, lineId,
                affectedSection.getUpStation().getId(),
                affectedSection.getDownStation().getId(),
                affectedSection.getDistance());
    }
}
