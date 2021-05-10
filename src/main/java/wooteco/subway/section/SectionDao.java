package wooteco.subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final KeyHolder keyHolder = new GeneratedKeyHolder();

    public SectionDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(final Long lineId,
                     final Long upStationId,
                     final Long downStationId,
                     final int distance) {
        final String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(con -> {
            final PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, upStationId);
            ps.setLong(3, downStationId);
            ps.setInt(4, distance);
            return ps;
        }, keyHolder);

        return keyHolder.getKeyAs(Long.class);
    }

    public int findDistance(final Long lineId, final Long upStationId, final Long downStationId){
        final String sql = "SELECT distance FROM SECTION WHERE line_id = ? AND up_station_id = ? AND down_station_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, lineId, upStationId, downStationId);
    }

    public void updateUpStation(final Long lineId, final Long before, final Long after, final int distance) {
        final String sql = "UPDATE SECTION SET up_station_id = ?, distance = ? WHERE up_station_id = ? AND line_id = ?";
        jdbcTemplate.update(sql, after, distance, before, lineId);
    }

    public void updateDownStation(final Long lineId, final Long before, final Long after, final int distance) {
        final String sql = "UPDATE SECTION SET down_station_id = ?, distance = ?  WHERE down_station_id = ? AND line_id = ?";
        jdbcTemplate.update(sql, after, distance, before, lineId);
    }

    public Long upStationIdOf(final Long lineId, final Long downStationId) {
        final String sql = "SELECT up_station_id from SECTION WHERE line_id = ? AND down_station_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, lineId, downStationId);
    }

    public Long downStationIdOf(final Long lineId, final Long upStationId) {
        final String sql = "SELECT down_station_id from SECTION WHERE line_id = ? AND up_station_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, lineId, upStationId);
    }

    public boolean isExistingSection(final Long lineId, final Long upStationId, final Long downStation) {
        final String sql = "SELECT EXISTS(SELECT from SECTION WHERE line_id = ? AND up_station_id = ? AND down_station_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, lineId, upStationId, downStation);
    }

    public boolean isExistingStation(final Long lineId, final Long stationId) {
        final String sql = "SELECT EXISTS(SELECT from SECTION WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?))";
        return jdbcTemplate.queryForObject(sql, Boolean.class, lineId, stationId, stationId);
    }

    public boolean isExistingUpStation(final Long lineId, final Long stationId) {
        final String sql = "SELECT EXISTS(SELECT from SECTION WHERE line_id = ? AND up_station_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, lineId, stationId);
    }

    public boolean isExistingDownStation(final Long lineId, final Long stationId) {
        final String sql = "SELECT EXISTS(SELECT from SECTION WHERE line_id = ? AND down_station_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, lineId, stationId);
    }

    public void deleteSection(final Long lineId, final Long upStationId, final Long downStationId) {
        final String sql = "DELETE FROM SECTION WHERE line_id = ? AND up_station_id = ? AND down_station_id = ?";
        jdbcTemplate.update(sql, lineId, upStationId, downStationId);
    }

    public Long stationCountInLine(final Long lineId){
        final String sql = "SELECT COUNT(*) from SECTION WHERE line_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, lineId) + 1;
    }
}
