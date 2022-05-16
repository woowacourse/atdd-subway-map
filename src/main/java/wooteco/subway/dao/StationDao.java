package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
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
public class StationDao {

    private static final RowMapper<Station> STATION_MAPPER = (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleInsert;

    public StationDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("STATION")
                .usingGeneratedKeyColumns("id");
    }

    public Station save(Station station) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(station);
        Long id = simpleInsert.executeAndReturnKey(parameters).longValue();
        return new Station(id, station.getName());
    }

    public List<Station> findAll() {
        String sql = "SELECT * FROM STATION";
        return jdbcTemplate.query(sql, STATION_MAPPER);
    }

    public Optional<Station> findById(Long id) {
        String sql = "SELECT * FROM STATION WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        return jdbcTemplate.query(sql, parameters, STATION_MAPPER)
                .stream()
                .findAny();
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM STATION WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, parameters);
    }

    public boolean existsId(Long id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM STATION WHERE id = :id)";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        return Integer.valueOf(1).equals(jdbcTemplate.queryForObject(sql, parameters, Integer.class));
    }

    public boolean existsName(Station station) {
        String sql = "SELECT EXISTS(SELECT 1 FROM STATION WHERE name = :name AND id != :id)";
        SqlParameterSource parameters = decideParametersForExists(station);
        return Integer.valueOf(1).equals(jdbcTemplate.queryForObject(sql, parameters, Integer.class));
    }

    public boolean existsContainingSection(Long id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM SECTION WHERE up_station_id = :id OR down_station_id = :id)";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        return Integer.valueOf(1).equals(jdbcTemplate.queryForObject(sql, parameters, Integer.class));
    }

    private SqlParameterSource decideParametersForExists(Station station) {
        if (station.getId() == null) {
            return new BeanPropertySqlParameterSource(new Station(0L, station.getName()));
        }
        return new BeanPropertySqlParameterSource(station);
    }

}
