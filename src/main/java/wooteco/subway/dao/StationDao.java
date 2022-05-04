package wooteco.subway.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import wooteco.subway.domain.Station;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

@Repository
public class StationDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) ->
            new Station(
                    resultSet.getLong("id"),
                    resultSet.getString("name")
            );

    public StationDao(final DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    public Station save(final String name) {
        checkDuplicateName(name);
        SqlParameterSource parameters = new MapSqlParameterSource("name", name);
        Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Station(id, name);
    }

    private void checkDuplicateName(String name) {
        if (findByName(name).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 지하철역 이름입니다.");
        }
    }

    private Optional<Station> findByName(String name) {
        String sql = "SELECT * FROM station WHERE name = :name";
        MapSqlParameterSource parameters = new MapSqlParameterSource("name", name);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, parameters, stationRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void deleteAll() {
        String sql = "TRUNCATE TABLE station";
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource());
        String resetIdSql = "ALTER TABLE station ALTER COLUMN id RESTART WITH 1";
        namedParameterJdbcTemplate.update(resetIdSql, new MapSqlParameterSource());
    }

    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        return namedParameterJdbcTemplate.query(sql, stationRowMapper);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM station WHERE id = :id";
        MapSqlParameterSource parameters = new MapSqlParameterSource("id", id);
        namedParameterJdbcTemplate.update(sql, parameters);
    }
}
