package wooteco.subway.domain.section;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

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

        // todo 저장실패 예외처리 필요 - 예외: line_id, up_station_id DB에 없음 등
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setLong(4, section.getDistance());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<Section> listByLineId(Long lineId) {
        final String sql = "SELECT id, line_id, up_station_id, down_station_id, distance FROM section WHERE line_id = ?";
        return jdbcTemplate.query(sql, sectionRowMapper, lineId);
    }

    public Long countOfSection(Long lineId, Long stationId) {
        final String sql = "SELECT COUNT(*) "
                + "FROM section "
                + "WHERE line_id = :line_id "
                + "AND "
                + "(up_station_id = :station_id OR down_station_id = :station_id)";

        final Map<String, Object> namedParams = new HashMap<>();
        namedParams.put("line_id", lineId);
        namedParams.put("station_id", stationId);

        return namedParameterJdbcTemplate.queryForObject(sql, namedParams, Long.class);
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
        // todo assert 삭제
        assert result.size() == 1;
        return Optional.of(result.get(0));
    }
}
