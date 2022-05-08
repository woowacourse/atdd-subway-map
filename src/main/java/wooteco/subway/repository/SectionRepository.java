package wooteco.subway.repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionEdge;

@Repository
public class SectionRepository {

    private static final RowMapper<Section> ROW_MAPPER = (rs, rn) -> {
        long id = rs.getLong("id");
        long lineId = rs.getLong("line_id");
        long upStationId = rs.getLong("up_station_id");
        long downStationId = rs.getLong("down_station_id");
        int distance = rs.getInt("distance");
        return new Section(id, lineId, new SectionEdge(upStationId, downStationId, distance));
    };

    private final JdbcTemplate jdbcTemplate;

    public SectionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                .prepareStatement(
                    "INSERT INTO SECTION(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)",
                    new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        return new Section(id, section.getLineId(), new SectionEdge(section.getUpStationId(),
            section.getDownStationId(), section.getDistance()));
    }

    public List<Section> findAllByLineId(Long lineId) {
        String sql = "SELECT id, line_id, up_station_id, down_station_id, distance "
            + "FROM SECTION WHERE line_id = ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, lineId);
    }

    public Optional<Section> findByLineIdAndUpStationId(Long lineId, Long upStationId) {
        try {
            String sql = "SELECT id, line_id, up_station_id, down_station_id, distance "
                + "FROM SECTION WHERE line_id = ? AND up_station_id = ?";
            Section section = jdbcTemplate.queryForObject(sql, ROW_MAPPER, lineId, upStationId);
            return Optional.of(section);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Section> findByLineIdAndDownStationId(Long lineId, Long downStationId) {
        try {
            String sql = "SELECT id, line_id, up_station_id, down_station_id, distance "
                + "FROM SECTION WHERE line_id = ? AND down_station_id = ?";
            Section section = jdbcTemplate.queryForObject(sql, ROW_MAPPER, lineId, downStationId);
            return Optional.of(section);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int findCountByLineId(Long lineId) {
        return jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM SECTION WHERE line_id = ?", Integer.class, lineId);
    }

    public boolean existByLineIdAndStationId(Long lineId, Long stationId) {
        String sql = "SELECT EXISTS "
            + "(SELECT id FROM SECTION WHERE line_id = ? AND "
            + "(up_station_id = ? OR down_station_id = ?))";
        return jdbcTemplate.queryForObject(sql, Boolean.class, lineId, stationId, stationId);
    }

    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM SECTION");
    }

    public void deleteByLineId(Long lineId) {
        jdbcTemplate.update("DELETE FROM SECTION WHERE line_id = ?", lineId);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM SECTION WHERE id = ?", id);
    }
}
