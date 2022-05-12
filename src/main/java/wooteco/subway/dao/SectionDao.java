package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;
    private final SectionMapper sectionMapper;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.sectionMapper = new SectionMapper();
    }

    public void save(final Long lineId, final Section section) {
        final String sql = "insert into Section (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql, lineId, section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public List<Section> findSectionsByLineId(final Long lineId) {
        final String sql = String.format("select * from Section where line_id = %d", lineId);
        return jdbcTemplate.query(sql, sectionMapper);
    }

    public void deleteAllByLine(final Long id) {
        final String sql = "delete from Section where line_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void editByUpStationId(final Long lineId, final Section section) {
        final String sql = "update Section set down_station_id = ?, distance = ? where line_id = ? and up_station_id = ?";
        jdbcTemplate.update(sql, section.getDownStationId(), section.getDistance(), lineId, section.getUpStationId());
    }

    public Section findSectionByUpStationId(final Long lineId, final Section section) {
        final String sql = String.format("select * from Section where line_id = %d and up_station_id = %d", lineId, section.getUpStationId());
        return jdbcTemplate.queryForObject(sql, sectionMapper);
    }

    public Section findSectionByDownStationId(final Long lineId, final Section section) {
        final String sql = String.format("select * from Section where line_id = %d and down_station_id = %d", lineId, section.getDownStationId());
        return jdbcTemplate.queryForObject(sql, sectionMapper);
    }

    public List<Section> findSectionsByStationId(final Long lineId, final Long stationId) {
        final String sql = String.format("select * from Section where line_id = %d and (up_station_id = %d or down_station_id = %d)",
                lineId, stationId, stationId);
        return jdbcTemplate.query(sql, sectionMapper);
    }

    public int countsByLine(final Long line_id) {
        final String sql = "select count(*) from Section where line_id = " + line_id;
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public void deleteSectionByStationId(final Long lineId, final Long stationId) {
        final String sql = "delete from Section where line_id = ? and (up_station_id = ? or down_station_id = ?)";
        jdbcTemplate.update(sql, lineId, stationId, stationId);
    }

    public void integrateSectionByStationId(final Long lineId, final Long stationId, final Section integrateTwoSections) {
        editByUpStationId(lineId, integrateTwoSections);
        deleteSectionByStationId(lineId, stationId);
    }

    private static final class SectionMapper implements RowMapper<Section> {
        public Section mapRow(final ResultSet rs, final int rowCnt) throws SQLException {
            return new Section(rs.getLong("up_station_id"), rs.getLong("down_station_id"), rs.getInt("distance"));
        }
    }
}
