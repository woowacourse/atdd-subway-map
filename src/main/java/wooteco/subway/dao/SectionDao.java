package wooteco.subway.dao;

import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
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

    public Optional<Integer> findDistance(long lineId, long upStationId, long downStationId) {
        Optional<Integer> distance = findDistanceByUpStationId(lineId, upStationId);
        if (distance.isPresent()) {
            return distance;
        }
        return findDistanceByDownStationId(lineId, downStationId);
    }

    private Optional<Integer> findDistanceByUpStationId(long lineId, long upStationId) {
        String sql = "SELECT distance FROM section WHERE line_id = :line_id AND up_station_id = :up_station_id";
        SqlParameterSource parameterSource = new MapSqlParameterSource("up_station_id", upStationId)
                .addValue("line_id", lineId);
        return getDistance(sql, parameterSource);
    }

    private Optional<Integer> findDistanceByDownStationId(long lineId, long downStationId) {
        String sql = "SELECT distance FROM section WHERE line_id = :line_id AND down_station_id = :down_station_id";
        SqlParameterSource parameterSource = new MapSqlParameterSource("down_station_id", downStationId)
                .addValue("line_id", lineId);
        return getDistance(sql, parameterSource);
    }

    private Optional<Integer> getDistance(String sql, SqlParameterSource parameterSource) {
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, parameterSource, Integer.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void deleteAll() {
        String sql = "TRUNCATE TABLE section";
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource());
        String resetIdSql = "ALTER TABLE section ALTER COLUMN id RESTART WITH 1";
        namedParameterJdbcTemplate.update(resetIdSql, new MapSqlParameterSource());
    }
}
