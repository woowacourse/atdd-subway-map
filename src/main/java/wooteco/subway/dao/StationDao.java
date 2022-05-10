package wooteco.subway.dao;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public StationDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> {
        Station station = new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
        return station;
    };

    public long save(Station station) {
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(station);
        return simpleJdbcInsert.executeAndReturnKey(parameterSource).longValue();
    }

    public List<Station> findAll() {
        String sql = "select * from STATION";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public void deleteById(Long stationId) {
        String sql = "delete from STATION where id = (?)";
        jdbcTemplate.update(sql, stationId);
    }

    public Station findById(Long stationId) {
        String sql = "select * from STATION where id = (?)";
        return jdbcTemplate.queryForObject(sql, stationRowMapper, stationId);
    }

    public boolean existByName(Station station) {
        String sql = "select exists (select * from STATION where name = (?))";
        return jdbcTemplate.queryForObject(sql, Boolean.class, station.getName());
    }
}
