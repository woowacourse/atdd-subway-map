package wooteco.subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.LineException;

import java.sql.PreparedStatement;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final KeyHolder keyHolder = new GeneratedKeyHolder();

    public SectionDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(final Long lineId,
                     final Long frontStationId,
                     final Long backStationId,
                     final int distance) {
        final String sql = "INSERT INTO SECTION (line_id, front_station_id, back_station_id, distance) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(con -> {
            final PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, frontStationId);
            ps.setLong(3, backStationId);
            ps.setInt(4, distance);
            return ps;
        }, keyHolder);

        return keyHolder.getKeyAs(Long.class);
    }

    public int findDistance(final Long lineId, final Long frontStationId, final Long backStationId) {
        try {
            final String sql = "SELECT distance FROM SECTION WHERE line_id = ? AND front_station_id = ? AND back_station_id = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, lineId, frontStationId, backStationId);
        } catch (Exception e) {
            throw new LineException("존재하지 않는 구간입니다.");
        }
    }

    public void updateFrontStation(final Long lineId, final Long before, final Long after, final int distance) {
        final String sql = "UPDATE SECTION SET front_station_id = ?, distance = ? WHERE front_station_id = ? AND line_id = ?";
        jdbcTemplate.update(sql, after, distance, before, lineId);
    }

    public void updateBackStation(final Long lineId, final Long before, final Long after, final int distance) {
        final String sql = "UPDATE SECTION SET back_station_id = ?, distance = ?  WHERE back_station_id = ? AND line_id = ?";
        jdbcTemplate.update(sql, after, distance, before, lineId);
    }

    public Long frontStationIdOf(final Long lineId, final Long backStationId) {
        final String sql = "SELECT front_station_id from SECTION WHERE line_id = ? AND back_station_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, lineId, backStationId);
    }

    public Long backStationIdOf(final Long lineId, final Long upStationId) {
        final String sql = "SELECT back_station_id from SECTION WHERE line_id = ? AND front_station_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, lineId, upStationId);
    }

    public boolean isExistingStation(final Long lineId, final Long stationId) {
        final String sql = "SELECT EXISTS(SELECT from SECTION WHERE line_id = ? AND (front_station_id = ? OR back_station_id = ?))";
        return jdbcTemplate.queryForObject(sql, Boolean.class, lineId, stationId, stationId);
    }

    public boolean isExistingFrontStation(final Long lineId, final Long stationId) {
        final String sql = "SELECT EXISTS(SELECT from SECTION WHERE line_id = ? AND front_station_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, lineId, stationId);
    }

    public boolean isExistingBackStation(final Long lineId, final Long stationId) {
        final String sql = "SELECT EXISTS(SELECT from SECTION WHERE line_id = ? AND back_station_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, lineId, stationId);
    }

    public void deleteSection(final Long lineId, final Long frontStationId, final Long backStationId) {
        final String sql = "DELETE FROM SECTION WHERE line_id = ? AND front_station_id = ? AND back_station_id = ?";
        jdbcTemplate.update(sql, lineId, frontStationId, backStationId);
    }

    public Long stationCountInLine(final Long lineId) {
        final String sql = "SELECT COUNT(*) from SECTION WHERE line_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, lineId) + 1;
    }

    public void save(Section section) {

    }
}
