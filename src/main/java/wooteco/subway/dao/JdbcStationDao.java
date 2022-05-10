package wooteco.subway.dao;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class JdbcStationDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;
    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> new Station(
        resultSet.getLong("id"),
        resultSet.getString("name")
    );

    public JdbcStationDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(dataSource)
            .withTableName("STATION")
            .usingGeneratedKeyColumns("id");
    }

    @Override
    public Long save(Station station) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(station);
        return insertActor.executeAndReturnKey(parameters).longValue();
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT * FROM STATION";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    @Override
    public void deleteById(Long stationId) {
        String sql = "DELETE FROM STATION WHERE id = (?)";
        jdbcTemplate.update(sql, stationId);
    }

    @Override
    public Station findById(Long stationId) {
        String sql = "SELECT * FROM STATION WHERE id = (?)";
        return jdbcTemplate.queryForObject(sql, stationRowMapper, stationId);
    }

    @Override
    public boolean existByName(Station station) {
        String sql = "SELECT EXISTS (SELECT * FROM STATION WHERE name = (?))";
        return jdbcTemplate.queryForObject(sql, Boolean.class, station.getName());
    }
}
