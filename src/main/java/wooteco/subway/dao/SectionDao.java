package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Sections findSectionsByLineId(Long lineId) {
        String sql = "SELECT id, up_station_id, down_station_id, distance FROM SECTION WHERE line_id = ?";

        List<Section> sections = jdbcTemplate.query(sql, (rs, rowNum) -> new Section(
                rs.getLong("id"),
                rs.getLong("up_station_id"),
                rs.getLong("down_station_id"),
                new Distance(rs.getInt("distance"))
        ), lineId);

        return Sections.from(sections);
    }

    public void saveSections(Long lineId, Sections sections) {
        removeAllSectionsByLineId(lineId);

        for (Section section : sections.getValue()) {
            save(lineId, section);
        }
    }

    private Section save(Long lineId, Section section) {
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance().getValue());
            return ps;
        }, keyHolder);

        Long createdId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Section(createdId, section);
    }

    private void removeAllSectionsByLineId(Long lineId) {
        String sql = "DELETE FROM SECTION WHERE line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }
}
