package wooteco.subway.section.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.section.domain.Section;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final KeyHolder keyHolder = new GeneratedKeyHolder();

    public SectionDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(final Line line, final int distance) {
        return save(line.getId(), line.getFirstStationId(), line.getLastStationId(), distance);
    }

    public Long save(final Section section) {
        return save(section.lineId(), section.frontStationId(), section.backStationId(), section.distance());
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

    public boolean isExistingStation(final Long stationId) {
        final String sql = "SELECT EXISTS(SELECT from SECTION WHERE (front_station_id = ? OR back_station_id = ?))";
        return jdbcTemplate.queryForObject(sql, Boolean.class, stationId, stationId);
    }

    public void delete(final Section section) {
        final String sql = "DELETE FROM SECTION WHERE id = ?";
        jdbcTemplate.update(sql, section.id());
    }

    public void deleteAllSectionInLine(final Long lineId) {
        final String sql = "DELETE FROM SECTION WHERE line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }

    public List<Section> findSections(final Long lineId) {
        final String sql = "SELECT * FROM SECTION WHERE line_id = ?";
        return jdbcTemplate.query(sql, rowMapper(), lineId);
    }

    private RowMapper<Section> rowMapper() {
        return (rs, rowNum) -> new Section(
                rs.getLong("id"),
                rs.getLong("line_id"),
                rs.getLong("front_station_id"),
                rs.getLong("back_station_id"),
                rs.getInt("distance")
        );
    }
}
