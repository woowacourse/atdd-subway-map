package wooteco.subway.dao;

import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SectionDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Section> eventRowMapper = (resultSet, rowNum)
            -> new Section(resultSet.getLong("id")
            , resultSet.getLong("line_id")
            , resultSet.getLong("up_station_id")
            , resultSet.getLong("down_station_id")
            , resultSet.getInt("distance")
    );

    public List<Section> findAll() {
        String sql = "select * from SECTION";
        return jdbcTemplate.query(sql, eventRowMapper);
    }

    public List<Section> findByLineId(Long id) {
        String sql = "select * from SECTION where line_id = :id";
        SqlParameterSource source = new MapSqlParameterSource("id", id);
        return jdbcTemplate.query(sql, source, eventRowMapper);
    }

    public List<Section> findByStationId(Long id) {
        String sql = "select * from SECTION where up_station_id = :id or down_station_id = :id";
        SqlParameterSource source = new MapSqlParameterSource("id", id);
        return jdbcTemplate.query(sql, source, eventRowMapper);
    }

    public List<Section> findByLineIdAndStationId(Long lineId, Long stationId) {
        String sql = "select * from SECTION where line_id = :lineId and (up_station_id = :stationId or down_station_id = :stationId)";
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("lineId", lineId);
        source.addValue("stationId", stationId);
        return jdbcTemplate.query(sql, source, eventRowMapper);
    }

    public Long save(Section section) {
        String sql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) "
                + "values (:lineId, :upStationId, :downStationId, :distance)";
        SqlParameterSource source = new BeanPropertySqlParameterSource(section);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, source, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public void update(Section section) {
        String sql = "update SECTION set down_station_id = :downStationId, up_station_id = :upStationId, distance = :distance where id = :id";

        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("id", section.getId());
        source.addValue("upStationId", section.getUpStationId());
        source.addValue("downStationId", section.getDownStationId());
        source.addValue("distance", section.getDistance());

        jdbcTemplate.update(sql, source);
    }

    public void deleteByLineId(Long lineId) {
        String sql = "delete from SECTION where line_id = :lineId";
        MapSqlParameterSource source = new MapSqlParameterSource("lineId", lineId);
        jdbcTemplate.update(sql, source);
    }

    public void deleteById(Long id) {
        String sql = "delete from SECTION where id = :id";
        MapSqlParameterSource source = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, source);
    }
}
