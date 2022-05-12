package wooteco.subway.repository.dao;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import wooteco.subway.repository.dao.entity.section.SectionEntity;

@Component
public class SectionDao {

    private static final RowMapper<SectionEntity> ROW_MAPPER =
            (resultSet, rowNum) -> new SectionEntity(
                    resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance")
            );

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public SectionDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("Section")
                .usingGeneratedKeyColumns("id");
    }

    public Long save(SectionEntity sectionEntity) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(sectionEntity);
        return jdbcInsert.executeAndReturnKey(parameters)
                .longValue();
    }

    public List<SectionEntity> findAllByLineId(Long lineId) {
        String query = "SELECT id, line_id, up_station_id, down_station_id, distance from Section"
                + " where line_id=(:lineId)";
        SqlParameterSource parameters = new MapSqlParameterSource("lineId", lineId);
        return jdbcTemplate.query(query, parameters, ROW_MAPPER);
    }

    public List<Long> findAllIdByLineId(Long lineId) {
        String query = "SELECT id from Section where line_id=(:lineId)";
        SqlParameterSource parameters = new MapSqlParameterSource("lineId", lineId);
        return jdbcTemplate.query(query, parameters,
                (resultSet, rowNum) -> resultSet.getLong("id"));
    }

    public Boolean existsById(Long id) {
        String query = "SELECT EXISTS(SELECT id FROM Section WHERE id=(:id)) as existable";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        return jdbcTemplate.queryForObject(query, parameters,
                (resultSet, rowNum) -> resultSet.getBoolean("existable"));
    }

    public Boolean existsByStationId(Long stationId) {
        String query = "SELECT EXISTS(SELECT id FROM Section"
                + " WHERE up_station_id=(:upStationId) or down_station_id=(:downStationId)) as existable";
        SqlParameterSource parameters = new MapSqlParameterSource("upStationId", stationId)
                .addValue("downStationId", stationId);
        return jdbcTemplate.queryForObject(query, parameters,
                (resultSet, rowNum) -> resultSet.getBoolean("existable"));
    }

    public void remove(Long id) {
        String query = "DELETE FROM Section WHERE id=(:id)";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(query, parameters);
    }
}
