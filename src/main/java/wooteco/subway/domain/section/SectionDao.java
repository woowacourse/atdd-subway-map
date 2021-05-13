package wooteco.subway.domain.section;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.web.exception.SubwayHttpException;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Section> sectionRowMapper = (rs, rowNum) -> new Section(
            rs.getLong("id"),
            rs.getLong("line_id"),
            rs.getLong("up_station_id"),
            rs.getLong("down_station_id"),
            rs.getInt("distance")
    );
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate,
            NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Long save(Section section) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "INSERT INTO section "
                + "(line_id, up_station_id, down_station_id, distance) "
                + "VALUES (?, ?, ?, ?)";

        try {
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                ps.setLong(1, section.getLineId());
                ps.setLong(2, section.getUpStationId());
                ps.setLong(3, section.getDownStationId());
                ps.setLong(4, section.getDistance());
                return ps;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new SubwayHttpException("중복된 역 이름입니다");
        }

        return keyHolder.getKey().longValue();
    }

    public List<Section> listByLineId(Long lineId) {
        final String sql = "SELECT id, line_id, up_station_id, down_station_id, distance FROM section WHERE line_id = ?";
        return jdbcTemplate.query(sql, sectionRowMapper, lineId);
    }

    public Long countSection(Long lineId, Long stationId) {
        final String sql = "SELECT COUNT(*) "
                + "FROM section "
                + "WHERE line_id = ? "
                + "AND "
                + "(up_station_id = ? OR down_station_id = ?)";

        return jdbcTemplate.queryForObject(sql, Long.class,
                lineId, stationId, stationId);
    }

    public void delete(Long id) {
        final String sql = "DELETE FROM section WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Optional<Section> priorSection(Long lineId, Long upStationId, Long downStationId) {
        final String sql = "SELECT id, line_id, up_station_id, down_station_id, distance "
                + "FROM section "
                + "WHERE line_id = :line_id "
                + "AND "
                + "(up_station_id = :up_station_id OR down_station_id = :down_station_id)";

        final Map<String, Object> namedParams = new HashMap<>();
        namedParams.put("line_id", lineId);
        namedParams.put("up_station_id", upStationId);
        namedParams.put("down_station_id", downStationId);

        List<Section> result = namedParameterJdbcTemplate
                .query(sql, namedParams, sectionRowMapper);

        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    public List<Section> countSectionByStationId(Long lineId, Long stationId) {
        final String sql = "SELECT id, line_id, up_station_id, down_station_id, distance "
                + "FROM section "
                + "WHERE "
                + "line_id = ? "
                + "AND "
                + "(up_station_id = ? OR down_station_id = ?)";
        return jdbcTemplate.query(sql, sectionRowMapper,
                lineId, stationId, stationId);
    }

    public void deleteSectionByStationId(Long lineId, Long stationId) {
        final String sql = "DELETE FROM section "
                + "WHERE "
                + "line_id = ? "
                + "AND "
                + "(up_station_id = ? OR down_station_id = ?)";
        jdbcTemplate.update(sql,
                lineId, stationId, stationId);
    }
}
