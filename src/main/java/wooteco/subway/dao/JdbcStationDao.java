package wooteco.subway.dao;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class JdbcStationDao implements StationDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    private final RowMapper<Station> rowMapper = (rs, rowNum) ->
            new Station(
                    rs.getLong("id"),
                    rs.getString("name")
            );

    public JdbcStationDao(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("STATION")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Station save(Station station) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(station);
        long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        return new Station(id, station.getName());
    }

    @Override
    public Station findById(Long id) {
        String sql = "SELECT * FROM STATION WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT * FROM STATION";
        return namedParameterJdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public int delete(Long id) {
        String sql = "DELETE FROM STATION WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public List<Station> findByIds(List<Long> ids) {
        String sql = "SELECT * FROM STATION WHERE id IN (:ids)";
        MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);
        List<Station> query = namedParameterJdbcTemplate.query(sql, params, rowMapper);
        System.out.println(query);
        return query;
    }
}
