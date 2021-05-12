package wooteco.subway.section.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.Section;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class JdbcSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section save(Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(con -> {
            PreparedStatement pstmt = con.prepareStatement(query, new String[]{"id"});
            pstmt.setLong(1, section.getLineId());
            pstmt.setLong(2, section.getUpStationId());
            pstmt.setLong(3, section.getDownStationId());
            pstmt.setInt(4, section.getDistance());
            return pstmt;
        }, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Section(id, section);
    }

    @Override
    public List<Section> findAllByLineId(Long id) {
        String query = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(query, sectionRowMapper(), id);
    }

    private RowMapper<Section> sectionRowMapper() {
        return (rs, rowNum) -> new Section(
                rs.getLong("id"),
                rs.getLong("line_id"),
                rs.getLong("up_station_id"),
                rs.getLong("down_station_id"),
                rs.getInt("distance")
        );
    }

    @Override
    public void updateUpStationId(Section section, Long upStationId) {
        String query = "UPDATE section SET up_station_id = ?, distance = ? WHERE line_id = ? AND up_station_id = ?";
        jdbcTemplate.update(query, upStationId, section.getDistance(), section.getLineId(), section.getUpStationId());
    }


    @Override
    public void updateDownStationId(Section section, Long downStationId) {
        String query = "UPDATE section SET down_station_id = ?, distance = ? WHERE line_id = ? AND down_station_id = ?";
        jdbcTemplate.update(query, downStationId, section.getDistance(), section.getLineId(), section.getDownStationId());
    }

    @Override
    public void deleteByLineIdAndUpStationId(Long lineId, Long upStationId) {
        String query = "DELETE FROM section WHERE line_id = ? AND up_station_id = ?";
        jdbcTemplate.update(query, lineId, upStationId);
    }

    @Override
    public void deleteByLineIdAndDownStationId(Long lineId, Long downStationId) {
        String query = "DELETE FROM section WHERE line_id = ? AND down_station_id = ?";
        jdbcTemplate.update(query, lineId, downStationId);
    }

    @Override
    public void deleteBySection(Section section) {
        String query = "DELETE FROM section WHERE line_id = ? AND up_station_id = ? AND down_station_id = ?";
        jdbcTemplate.update(query, section.getLineId(), section.getUpStationId(), section.getDownStationId());
    }
}