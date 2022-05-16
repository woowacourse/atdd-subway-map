package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public StationDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("STATION")
                .usingGeneratedKeyColumns("id");
    }

    public Station insert(Station station) {
        final SqlParameterSource parameters = new BeanPropertySqlParameterSource(station);
        final Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Station(id, station.getName());
    }

    private Station findById(Long id) {
        String SQL = "select * from station where id = ?;";
        return jdbcTemplate.queryForObject(SQL, rowMapper(), id);
    }

    public List<Station> findAll() {
        String SQL = "select * from station;";
        return jdbcTemplate.query(SQL, rowMapper());
    }

    public List<Station> findAllByLineId(Long id) {
        String SQL = "select * from station join section on station.id = section.up_station_id " +
                "or station.id = section.down_station_id where line_id = ?";
        return jdbcTemplate.query(SQL, rowMapper(), id);
    }

    private RowMapper<Station> rowMapper() {
        return (rs, rowNum) -> {
            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            return new Station(id, name);
        };
    }

    public void deleteById(Long id) {
        findById(id);
        String SQL = "delete from station where id = ?";
        jdbcTemplate.update(SQL, id);
    }

    public boolean existStationById(Long id) {
        final String SQL = "select exists (select * from station where id = ?)";
        return jdbcTemplate.queryForObject(SQL, Boolean.class, id);
    }

    public boolean existStationByName(String name) {
        final String SQL = "select exists (select * from station where name = ?)";
        return jdbcTemplate.queryForObject(SQL, Boolean.class, name);
    }

    public boolean existStationInSections(Long id) {
        final String SQL = "select exists (select * from station join section on " +
                "section.up_station_id = station.id or section.down_station_id = station.id " +
                "where station.id = ?)";
        return jdbcTemplate.queryForObject(SQL, Boolean.class, id);
    }
}
