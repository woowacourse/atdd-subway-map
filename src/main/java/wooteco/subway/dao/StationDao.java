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

import wooteco.subway.dao.dto.StationDto;
import wooteco.subway.domain.Station;

@Repository
public class StationDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) ->
            new Station(
                    resultSet.getLong("id"),
                    resultSet.getString("name")
            );

    public StationDao(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    public Station save(Station station) {
        StationDto stationDto = new StationDto(station);
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(stationDto);
        Long id = simpleJdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return new Station(id, station.getName());
    }

    public Station save(String name) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("name", name);
        Long id = simpleJdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return new Station(id, name);
    }

    public Optional<Station> findById(long id) {
        String sql = "SELECT * FROM station WHERE id = :id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(
                    namedParameterJdbcTemplate.queryForObject(sql, parameterSource, stationRowMapper));
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
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        namedParameterJdbcTemplate.update(sql, parameterSource);
    }
}
