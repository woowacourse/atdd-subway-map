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
public class StationDaoImpl implements StationDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public StationDaoImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    private static final RowMapper<Station> actorRowMapper = (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    @Override
    public Station save(Station station) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(station);
        Long id = insertActor.executeAndReturnKey(parameters).longValue();
        return new Station(id, station.getName());
    }

    @Override
    public List<Station> findAll() {
        String sql = "select * from station";
        return namedParameterJdbcTemplate.query(sql, actorRowMapper);
    }

    @Override
    public int deleteById(Long id) {
        String sql = "delete from station where id = :id";
        SqlParameterSource namedParameter = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.update(sql, namedParameter);
    }

    @Override
    public boolean exists(Station station) {
        String sql = "select exists (select name from station where name = :name)";
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(station);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Boolean.class));
    }
}
