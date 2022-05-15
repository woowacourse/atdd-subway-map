package wooteco.subway.reopository.dao;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class JdbcStationDao {

    private static final RowMapper<Station> mapper = (rs, rowNum) ->
            new Station(
                    rs.getLong("id"),
                    rs.getString("name")
            );

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;


    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    public Station save(Station station) {
        System.out.println("hello");
        SqlParameterSource parameters = new MapSqlParameterSource("name", station.getName());
        long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Station(id, station.getName());
    }

    public List<Station> findAll() {
        String sql = "select * from station";
        return jdbcTemplate.query(sql, mapper);
    }

    public void deleteById(Long id) {
        String sql = "delete from station where id = (?)";
        jdbcTemplate.update(sql, id);
    }

    public void deleteAll() {
        String sql = "delete from station";
        jdbcTemplate.update(sql);
    }

    public boolean existByName(String name) {
        String sql = "select exists( select * from station where name = (?))";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count != 0;
    }
}
