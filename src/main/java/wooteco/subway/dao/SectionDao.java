package wooteco.subway.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

@Repository
public class SectionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SectionDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        String sql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (:line_id, :up_station_id, :down_station_id, :distance)";

        Map<String, Object> params = new HashMap<>();
        params.put("line_id", section.getLineId());
        params.put("up_station_id", section.getUpStationId());
        params.put("down_station_id", section.getDownStationId());
        params.put("distance", section.getDistance());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);

        long sectionId = keyHolder.getKey().longValue();

        return new Section(sectionId, section.getLineId(), section.getUpStationId(), section.getDownStationId(),
                section.getDistance());
    }

    public Sections findAllByLineId(Long id) {
        String sql = "SELECT * FROM section WHERE line_id = :line_id";

        Map<String, Object> params = new HashMap<>();
        params.put("line_id", id);

        return new Sections(jdbcTemplate.query(sql, params, getRowMapper()));
    }

    public Optional<Section> findBy(Long lineId, Long upStationId, Long downStationId) {
        String sql = "SELECT * FROM section WHERE line_id = :line_id AND (up_station_id = :up_station_id OR down_station_id = :down_station_id)";

        Map<String, Object> params = new HashMap<>();
        params.put("line_id", lineId);
        params.put("up_station_id", upStationId);
        params.put("down_station_id", downStationId);

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, params, getRowMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM section WHERE id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        jdbcTemplate.update(sql, params);
    }

    private RowMapper<Section> getRowMapper() {
        return (rs, rowNum) -> new Section(rs.getLong("id"), rs.getLong("line_id"), rs.getLong("up_station_id"),
                rs.getLong("down_station_id"), rs.getInt("distance"));
    }
}
