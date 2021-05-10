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

    public boolean isExistingStation(Long id) {
        String sql = "select count(*) from SECTION where UP_STATION_ID = ? or DOWN_STATION_ID = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, id, id) != 0;
    }

    public Optional<Section> findSectionByDownStationId(Long downStationId) {
        String sql = "select * from SECTION where DOWN_STATION_ID = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, sectionRowMapper(), downStationId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Section> findSectionByUpStationId(Long upStationId) {
        String sql = "select * from SECTION where UP_STATION_ID = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, sectionRowMapper(), upStationId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean hasEndPointInSection(Long upStationId, Long downStationId) {
        String upStationIdCondition = "select count(*) from SECTION where UP_STATION_ID = ?";
        String downStationIdCondition = "select count(*) from SECTION where DOWN_STATION_ID = ?";

        return endPointCondition(upStationIdCondition, downStationIdCondition, upStationId)
                || endPointCondition(downStationIdCondition, upStationIdCondition, downStationId);
    }

    private boolean endPointCondition(String query, String query2, Long id) {
        return jdbcTemplate.queryForObject(query, Integer.class, id) == 1 &&
                jdbcTemplate.queryForObject(query2, Integer.class, id) == 0;
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
