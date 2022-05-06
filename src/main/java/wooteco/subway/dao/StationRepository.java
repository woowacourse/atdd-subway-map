package wooteco.subway.dao;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class StationRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;


    public StationRepository(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    public Station save(final Station station) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", station.getName());
        Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Station(id, station.getName());
    }

    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        RowMapper<Station> stationRowMapper = rowMapper();
        return namedParameterJdbcTemplate.query(sql, stationRowMapper);
    }

    public void deleteById(final Long id) {
        String sql = "DELETE FROM station WHERE id = :id";
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource("id", id));
    }

    public Station findByName(final String name) {
        String sql = "SELECT * FROM station WHERE name = :name";
        SqlParameterSource parameters = new MapSqlParameterSource("name", name);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, parameters, rowMapper());
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public Station findById(Long id) {
        String sql = "SELECT * FROM station WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, parameters, rowMapper());
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    private RowMapper<Station> rowMapper() {
        return (resultSet, rowNum) -> {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            return new Station(id, name);
        };
    }
}
