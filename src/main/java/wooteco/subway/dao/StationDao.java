package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class StationDao {

    private static final String NON_EXISTENT_ID_EXCEPTION = "존재하지 않는 id입니다.";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("station").usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", station.getName());

        final Number number = simpleJdbcInsert.executeAndReturnKey(parameters);
        return new Station(number.longValue(), station.getName());
    }

    public List<Station> findAll() {
        final String sql = "SELECT * FROM station";

        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public boolean existsByName(String name) {
        final String sql = "select count(*) from station where name = ?";
        final Integer numOfStation = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return !numOfStation.equals(0);
    }

    public void deleteById(Long id) {
        final String sql = "DELETE FROM station where id = ?";
        validateResult(jdbcTemplate.update(sql, id));
    }

    private void validateResult(int result) {
        if (result == 0) {
            throw new IllegalArgumentException(NON_EXISTENT_ID_EXCEPTION);
        }
    }

    public Station findById(Long id) {
        final String sql = "SELECT * FROM station WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, stationRowMapper, id);
    }
}
