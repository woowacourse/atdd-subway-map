package wooteco.subway.station.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.domain.Station;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class StationH2Dao implements StationDao{

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) ->
        new Station(resultSet.getLong("id"), resultSet.getString("name"));


    public StationH2Dao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Station save(Station station) {
        String insertQuery = "INSERT INTO station(name) VALUES(?);";
        jdbcTemplate.update(insertQuery, station.getName());

        String findQuery = "SELECT * FROM station WHERE name = ?;";
        return jdbcTemplate.queryForObject(findQuery, stationRowMapper, station.getName());
    }

    @Override
    public Optional<Station> findById(Long stationId) {
        String findQuery = "SELECT * FROM station WHERE id = ?;";
        return jdbcTemplate.queryForList(findQuery, Station.class, stationId).stream().findAny();
    }

    @Override
    public Optional<Station> findByName(String stationName) {
        String findQuery = "SELECT * FROM station WHERE name = ?;";
        return jdbcTemplate.queryForList(findQuery, Station.class, stationName).stream().findAny();
    }

    @Override
    public List<Station> findAll() {
        String findQuery = "SELECT * FROM station;";
        return jdbcTemplate.query(findQuery, stationRowMapper);
    }

    @Override
    public void delete(Long id) {
        String findQuery = "DELETE FROM station WHERE id = ?;";
        jdbcTemplate.update(findQuery, id);
    }

    @Override
    public void clear() {
        String findQuery = "TRUNCATE TABLE station;";
        jdbcTemplate.update(findQuery);
    }
}
