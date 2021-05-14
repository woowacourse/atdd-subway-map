package wooteco.subway.station.dao;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.naming.Name;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.domain.Station;

@Primary
@Repository
public class StationH2Dao implements StationDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) ->
        Station.of(resultSet.getLong("id"), resultSet.getString("name"));


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
        return jdbcTemplate.query(findQuery, stationRowMapper, stationId).stream().findAny();
    }

    @Override
    public Optional<Station> findByName(String stationName) {
        String findQuery = "SELECT * FROM station WHERE name = ?;";
        return jdbcTemplate.query(findQuery, stationRowMapper, stationName).stream().findAny();
    }

    @Override
    public List<Station> findAllByIds(Set<Long> stationIds) {
        String findAllByIdsQuery = "SELECT * FROM station WHERE id IN(%s);";
        String inQuery = String.join(",", Collections.nCopies(stationIds.size(), "?"));
        return jdbcTemplate.query(String.format(findAllByIdsQuery, inQuery), stationRowMapper, stationIds.toArray());
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
}
