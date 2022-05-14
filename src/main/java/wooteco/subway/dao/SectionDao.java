package wooteco.subway.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.section.NoSuchSectionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SectionDao {

    private static final RowMapper<Section> rowMapper = (rs, rowNum)
            -> new Section(rs.getLong("id"),
            rs.getInt("distance"),
            rs.getLong("line_id"),
            rs.getLong("up_station_id"),
            rs.getLong("down_station_id")
    );

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SectionDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        String sql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) " +
                "VALUES (:lineId, :upStationId, :downStationId, :distance)";

        Map<String, Object> params = new HashMap<>();
        params.put("lineId", section.getLineId());
        params.put("upStationId", section.getUpStationId());
        params.put("downStationId", section.getDownStationId());
        params.put("distance", section.getDistance());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        long sectionId = keyHolder.getKey().longValue();

        return new Section(sectionId, section.getDistance(), section.getLineId(), section.getUpStationId(), section.getDownStationId());
    }

    public List<Section> findAll() {
        String sql = "SELECT * FROM section";
        return jdbcTemplate.query(sql, new MapSqlParameterSource(), rowMapper);
    }

    public List<Section> findByLineId(Long lineId) {
        String sql = "SELECT * FROM section WHERE line_id = :lineId";
        Map<String, Object> params = new HashMap<>();
        params.put("lineId", lineId);
        return jdbcTemplate.query(sql, new MapSqlParameterSource(params), rowMapper);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM section WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        int affected = jdbcTemplate.update(sql, params);

        if (affected == 0) {
            throw new NoSuchSectionException();
        }
    }

    public void update(Section section) {
        String sql = "UPDATE section " +
                "SET up_station_id = :upStationId, down_station_id = :downStationId, distance = :distance " +
                "WHERE id = :id";

        jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(section));
    }
}
