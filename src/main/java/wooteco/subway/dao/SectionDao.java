package wooteco.subway.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) -> new Section(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance"));

    public Long save(Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "INSERT INTO SECTION(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update((Connection conn) -> {
            PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id"});
            pstmt.setLong(1, section.getLineId());
            pstmt.setLong(2, section.getUpStationId());
            pstmt.setLong(3, section.getDownStationId());
            pstmt.setInt(4, section.getDistance());
            return pstmt;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public boolean existByLineIdAndDownStationId(Long lineId, Long downStationId) {
        final String sql = "SELECT EXISTS(SELECT * FROM SECTION WHERE line_id = ? AND down_station_id = ?) AS SUCCESS";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, lineId, downStationId));
    }

    public boolean existByLineIdAndUpStationId(Long lineId, Long upStationId) {
        final String sql = "SELECT EXISTS(SELECT * FROM SECTION WHERE line_id = ? AND up_station_id = ?) AS SUCCESS";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, lineId, upStationId));
    }

    public Optional<Section> findByUpOrDownStationId(Long lineId, Long upStationId, Long downStationId) {
        final String sql = "SELECT * FROM SECTION WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";

        return Optional.ofNullable(
                jdbcTemplate.queryForObject(sql, sectionRowMapper, lineId, upStationId, downStationId));
    }

    public void updateUpStationId(Long id, Long upStationId, int distance) {
        final String sql = "UPDATE SECTION SET up_station_id = ?, distance = ? WHERE id = ?";
        jdbcTemplate.update(sql, upStationId, distance, id);
    }

    public void updateDownStationId(Long id, Long downStationId, int distance) {
        final String sql = "UPDATE SECTION SET down_station_id = ?, distance = ? WHERE id = ?";
        jdbcTemplate.update(sql, downStationId, distance, id);
    }

    public Section findById(Long id) {
        final String sql = "SELECT id, line_id, up_station_id, down_station_id, distance FROM SECTION WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, sectionRowMapper, id);
    }
}
