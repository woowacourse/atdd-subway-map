package wooteco.subway.dao;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.station.StationNotExistException;

import java.util.*;

@Repository
public class StationDao {

    private static final RowMapper<Station> STATION_MAPPER = (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station insert(String name) {
        final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>(1);
        params.put("name", name);
        Long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        return new Station(id, name);
    }

    public List<Station> findAll() {
        String query = "SELECT * FROM station";
        return jdbcTemplate.query(query, STATION_MAPPER);
    }

    public Station findByName(String name) {
        String query = "SELECT * FROM station WHERE name=?";
        final Station station = DataAccessUtils.singleResult(jdbcTemplate.query(query, STATION_MAPPER, name));
        if (Objects.isNull(station)) {
            throw new StationNotExistException();
        }
        return station;
    }

    public void deleteById(Long id) {
        String query = "DELETE FROM station WHERE id = ?";
        jdbcTemplate.update(query, id);
    }
}
