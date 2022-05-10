package wooteco.subway.repository.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.repository.entity.SectionEntity;

@Repository
public class JdbcSectionDao {

    private static final RowMapper<SectionEntity> rowMapper = (resultSet, rowNum) -> new SectionEntity(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance")
    );

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcSectionDao(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public SectionEntity save(final SectionEntity sectionEntity) {
        final String sql = "insert into SECTION(line_id, up_station_id, down_station_id, distance)"
                + " values(:lineId, :upStationId, :downStationId, :distance)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final SqlParameterSource source = new BeanPropertySqlParameterSource(sectionEntity);
        jdbcTemplate.update(sql, source, keyHolder);
        final long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return sectionEntity.fillId(id);
    }

    public List<SectionEntity> findByLineId(final Long lineId) {
        final String sql = "select id, line_id, up_station_id, down_station_id, distance"
                + " from SECTION"
                + " where line_id = :lineId";
        final Map<String, Long> params = new HashMap<>();
        params.put("lineId", lineId);
        final SqlParameterSource source = new MapSqlParameterSource(params);
        return jdbcTemplate.query(sql, source, rowMapper);
    }

    public void update(final SectionEntity sectionEntity) {
        final String sql = "update SECTION set"
                + " up_station_id = :upStationId,"
                + " down_station_id = :downStationId,"
                + " distance = :distance"
                + " where id = :id and line_id = :lineId";
        final SqlParameterSource source = new BeanPropertySqlParameterSource(sectionEntity);
        jdbcTemplate.update(sql, source);
    }
}
