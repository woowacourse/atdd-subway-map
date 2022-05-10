package wooteco.subway.dao;

import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import wooteco.subway.entity.SectionEntity;

@Repository
public class SectionDao {

    private static final RowMapper<SectionEntity> ROW_MAPPER = (resultSet, rowNum) ->
            new SectionEntity(resultSet.getLong("line_id"),
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance"));

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SectionDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SectionEntity> findAllByLineId(Long lineId) {
        final String sql = "SELECT line_id, up_station_id, down_station_id, distance FROM section "
                + "WHERE line_id = :lineId";

        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("lineId", lineId);

        return jdbcTemplate.query(sql, paramSource, ROW_MAPPER);
    }

    public void save(SectionEntity sectionEntity) {
        final String sql = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) "
                + "VALUES(:lineId, :upStationId, :downStationId, :distance)";
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(sectionEntity);

        jdbcTemplate.update(sql, paramSource);
    }

    public void deleteAllByLineId(Long lineId) {
        final String sql = "DELETE FROM section WHERE line_id = :lineId ";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("lineId", lineId);

        jdbcTemplate.update(sql, paramSource);
    }

    public void deleteAllByLineIdAndStationId(Long lineId, Long stationId) {
        final String sql = "DELETE FROM section WHERE line_id = :lineId "
                + "AND (up_station_id = :stationId OR down_station_id = :stationId)";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("lineId", lineId);
        paramSource.addValue("stationId", stationId);

        jdbcTemplate.update(sql, paramSource);
    }
}
