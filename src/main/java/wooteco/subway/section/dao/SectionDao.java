package wooteco.subway.section.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Id;
import wooteco.subway.line.domain.Line;
import wooteco.subway.section.domain.Distance;
import wooteco.subway.section.domain.Section;
import wooteco.subway.station.domain.Station;

@Repository
public class SectionDao {

    private static final String ASSEMBLE_SECTION_SQL =
        "SELECT target_section.id AS id, "
            + "line.id AS line_id, line.name AS line_name, line.color AS line_color, "
            + "up_station.id AS up_station_id, up_station.name AS up_station_name, "
            + "down_station.id AS down_station_id, down_station.name AS down_station_name, "
            + "distance "
            + "FROM section AS target_section "
            + "LEFT JOIN line ON target_section.line_id = line.id "
            + "LEFT JOIN station AS up_station ON target_section.up_station_id = up_station.id "
            + "LEFT JOIN station AS down_station ON target_section.down_station_id = down_station.id";

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
            final String lineName = rs.getString("line_name");
            final String lineColor = rs.getString("line_color");
            final Long upStationId = rs.getLong("up_station_id");
            final String upStationName = rs.getString("up_station_name");
            final Long downStationId = rs.getLong("down_station_id");
            final String downStationName = rs.getString("down_station_name");
            final int distance = rs.getInt("distance");
            return new Section(
                new Id(foundId),
                new Line(lineId, lineName, lineColor),
                new Station(upStationId, upStationName),
                new Station(downStationId, downStationName),
                new Distance(distance)
            );
        };
    }

    public Section save(final Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("line_id", section.getLine().getId());
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());
        params.put("distance", section.getDistance().value());

        long key = jdbcInsert.executeAndReturnKey(params).longValue();

        return new Section(key, section);
    }

    public List<Section> findAllByLineId(final Long lineId) {
        String sql = ASSEMBLE_SECTION_SQL + " WHERE target_section.line_id = ?";

        return jdbcTemplate.query(sql, rowMapper, lineId);
    }

    public Optional<Section> findById(final Long sectionId) {
        try {
            String sql = ASSEMBLE_SECTION_SQL + " WHERE target_section.id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, sectionId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Section> findByLineIdAndUpStationId(final Long lineId, final Long upStationId) {
        try {
            String sql = ASSEMBLE_SECTION_SQL
                + " WHERE target_section.line_id = ? AND target_section.up_station_id = ?";
            return Optional
                .ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, lineId, upStationId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Section> findByLineIdAndDownStationId(final Long lineId,
        final Long downStationId) {
        try {
            String sql = ASSEMBLE_SECTION_SQL
                + " WHERE target_section.line_id = ? AND target_section.down_station_id = ?";
            return Optional
                .ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, lineId, downStationId));
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