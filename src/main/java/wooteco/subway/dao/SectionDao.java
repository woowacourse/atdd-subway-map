package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class SectionDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final RowMapper<SectionDto> sectionRowMapper = (resultSet, rowNum) ->
            new SectionDto(
                    resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance"));

    public SectionDao(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public SectionDto save(long lineId, long upStationId, long downStationId, int distance) {
        SectionDto section = new SectionDto(lineId, upStationId, downStationId, distance);
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(section);
        long id = simpleJdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return new SectionDto(id, lineId, upStationId, downStationId, distance);
    }

    public List<SectionDto> findById(long id) {
        String sql = "SELECT * FROM section WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.query(sql, parameterSource, sectionRowMapper);
    }

    public List<SectionDto> findAll() {
        String sql = "SELECT * FROM section";
        return namedParameterJdbcTemplate.query(sql, sectionRowMapper);
    }

    public Optional<Integer> findDistanceById(long id) {
        String sql = "SELECT distance FROM section WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, parameterSource, Integer.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updateUpStation(long id, long upStationId) {
        String sql = "UPDATE section SET up_station_id = :upStationId WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource("upStationId", upStationId)
                .addValue("id", id);
        namedParameterJdbcTemplate.update(sql, parameterSource);
    }

    public void updateDownStation(long id, long downStationId) {
        String sql = "UPDATE section SET down_station_id = :downStationId WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource("downStationId", downStationId)
                .addValue("id", id);
        namedParameterJdbcTemplate.update(sql, parameterSource);
    }

    public void updateDistance(long id, int distance) {
        String sql = "UPDATE section SET distance = :distance WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource("distance", distance)
                .addValue("id", id);
        namedParameterJdbcTemplate.update(sql, parameterSource);
    }

    public void deleteAll() {
        String sql = "TRUNCATE TABLE section";
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource());
        String resetIdSql = "ALTER TABLE section ALTER COLUMN id RESTART WITH 1";
        namedParameterJdbcTemplate.update(resetIdSql, new MapSqlParameterSource());
    }
}
