package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import javax.swing.text.html.Option;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.service.dto.DeleteStationDto;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final RowMapper<Section> rowMapper;

    public SectionDao(final JdbcTemplate jdbcTemplate, final DataSource source) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(source)
            .withTableName("SECTION")
            .usingGeneratedKeyColumns("id");
        this.rowMapper = (rs, rowNum) -> {
            final Long foundId = rs.getLong("id");
            final Long lineId = rs.getLong("line_id");
            final Long upStationId = rs.getLong("up_station_id");
            final Long downStationId = rs.getLong("down_station_id");
            final int distance = rs.getInt("distance");
            return new Section(foundId, lineId, upStationId, downStationId, distance);
        };
    }

    public Section save(final Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("line_id", section.getLineId());
        params.put("up_station_id", section.getUpStationId());
        params.put("down_station_id", section.getDownStationId());
        params.put("distance", section.getDistance());

        long key = jdbcInsert.executeAndReturnKey(params).longValue();

        return new Section(key, section);
    }

    public List<Section> findAllByLineId(final Long lineId) {
        String sql = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(sql, rowMapper, lineId);
    }

    public Optional<Section> findByLineIdAndUpStationId(final Long lineId, final Long upStationId) {
        try {
            String sql = "SELECT * FROM section WHERE line_id = ? AND up_station_id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, lineId, upStationId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Section> findByLineIdAndDownStationId(final Long lineId, final Long downStationId) {
        try {
            String sql = "SELECT * FROM section WHERE line_id = ? AND down_station_id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, lineId, downStationId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int deleteByLineIdAndUpStationId(final Long lineId, final Long upStationId) {
        String sql = "DELETE FROM section WHERE line_id = ? AND up_station_id = ?";
        return jdbcTemplate.update(sql, lineId, upStationId);
    }

    public int deleteByLineIdAndDownStationId(final Long lineId, final Long downStationId) {
        String sql = "DELETE FROM section WHERE line_id = ? AND down_station_id = ?";
        return jdbcTemplate.update(sql, lineId, downStationId);
    }

    public int delete(final Section section) {
        String sql = "DELETE FROM section WHERE id = ?";
        return jdbcTemplate.update(sql, section.getId());
    }
}
