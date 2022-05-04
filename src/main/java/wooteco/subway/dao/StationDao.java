package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

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

    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) ->
            new Station(
                    resultSet.getLong("id"),
                    resultSet.getString("name")
            );


    public Station save(String name) {
        Long id = simpleJdbcInsert.executeAndReturnKey(Map.of("name",name)).longValue();
        return new Station(id, name);
    }

    public List<Station> findAll() {
        final String sql = "SELECT * FROM STATION";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public void delete(Long id) {
        final String sql = "DELETE FROM STATION WHERE id=?";
        int count = jdbcTemplate.update(sql, id);
        if (count == 0) {
            new IllegalArgumentException("존재하지 않는 지하철역입니다.");
        }
    }

    public void validate(String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM STATION WHERE name=?)";
        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, name);
        if (result) {
            throw new IllegalArgumentException("지하철 이름이 중복될 수 없습니다.");
        }
    }

}
