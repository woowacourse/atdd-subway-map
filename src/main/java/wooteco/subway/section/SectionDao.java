package wooteco.subway.section;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Optional;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Long lineId, Long upStationId, Long downStationId, int distance) {
        String sql = "insert into SECTION (LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE) values(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, String.valueOf(lineId));
            ps.setString(2, String.valueOf(upStationId));
            ps.setString(3, String.valueOf(downStationId));
            ps.setString(4, String.valueOf(distance));
            return ps;
        }, keyHolder);
        return new Section(keyHolder.getKey().longValue(), lineId, upStationId, downStationId, distance);
    }

    public int delete(Long id) {
        String sql = "delete from SECTION where ID = ?";
        return jdbcTemplate.update(sql, id);
    }

    public int delete(Long lineId, Long stationId) {
        String sql = "delete from SECTION where LINE_ID = ? and (UP_STATION_ID = ? or DOWN_STATION_ID = ?)";
        return jdbcTemplate.update(sql, lineId, stationId, stationId);
    }

    public boolean isExistingStation(Long lineId, Long stationId) {
        String sql = "select count(*) from SECTION where LINE_ID = ? and (UP_STATION_ID = ? or DOWN_STATION_ID = ?)";
        return jdbcTemplate.queryForObject(sql, Integer.class, lineId, stationId, stationId) != 0;
    }

    public Optional<Section> findSectionByDownStationId(Long lineId, Long downStationId) {
        String sql = "select * from SECTION where LINE_ID = ? and DOWN_STATION_ID = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, sectionRowMapper(), lineId, downStationId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Section> findSectionByUpStationId(Long lineId, Long upStationId) {
        String sql = "select * from SECTION where LINE_ID = ? and UP_STATION_ID = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, sectionRowMapper(), lineId, upStationId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean hasEndStationInSection(Long lineId, Long upStationId, Long downStationId) {
        return isEndStation(lineId, upStationId) || isEndStation(lineId, downStationId);
    }

    public boolean isEndStation(Long lineId, Long stationId) {
        String upStationIdCondition = "select count(*) from SECTION where LINE_ID = ? and UP_STATION_ID = ?";
        String downStationIdCondition = "select count(*) from SECTION where LINE_ID = ? and DOWN_STATION_ID = ?";

        boolean condition1 = jdbcTemplate.queryForObject(upStationIdCondition, Integer.class, lineId, stationId) == 1
                && jdbcTemplate.queryForObject(downStationIdCondition, Integer.class, lineId, stationId) == 0;
        boolean condition2 = jdbcTemplate.queryForObject(upStationIdCondition, Integer.class, lineId, stationId) == 0
                && jdbcTemplate.queryForObject(downStationIdCondition, Integer.class, lineId, stationId) == 1;

        return condition1 || condition2;
    }

    public void updateDistanceAndDownStation(Long lineId, Long upStationId, Long downStationId, int distance) {
        String query = "update SECTION set DOWN_STATION_ID = ?, DISTANCE = ? where LINE_ID = ? and UP_STATION_ID = ?";
        jdbcTemplate.update(query, downStationId, distance, lineId, upStationId);
    }

    public int numberOfEnrolledSection(Long lineId) {
        String query = "select count(*) from SECTION where LINE_ID = ?";
        return jdbcTemplate.queryForObject(query, Integer.class, lineId);
    }

    private RowMapper<Section> sectionRowMapper() {
        return (rs, rowNum) ->
                new Section(
                        rs.getLong("id"),
                        rs.getLong("line_id"),
                        rs.getLong("up_station_id"),
                        rs.getLong("down_station_id"),
                        rs.getInt("distance")
                );
    }
}
