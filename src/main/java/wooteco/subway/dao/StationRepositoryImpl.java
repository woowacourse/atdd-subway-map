package wooteco.subway.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class StationRepositoryImpl implements StationRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    private RowMapper<Station> rowMapper() {
        return (resultSet, rowNum) -> {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            return new Station(id, name);
        };
    }

    public StationRepositoryImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Station save(final Station station) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", station.getName());
        Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Station(id, station.getName());
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        RowMapper<Station> stationRowMapper = rowMapper();
        return namedParameterJdbcTemplate.query(sql, stationRowMapper);
    }

    @Override
    public void deleteById(final Long id) {
        String sql = "DELETE FROM station WHERE id = :id";
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource("id", id));
    }

    @Override
    public Optional<Station> findByName(final String name) {
        String sql = "SELECT * FROM station WHERE name = :name";
        SqlParameterSource parameters = new MapSqlParameterSource("name", name);
        List<Station> stations = namedParameterJdbcTemplate.query(sql, parameters, rowMapper());
        return getOptional(stations);
    }

    @Override
    public Optional<Station> findById(Long id) {
        String sql = "SELECT * FROM station WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        List<Station> stations = namedParameterJdbcTemplate.query(sql, parameters, rowMapper());
        return getOptional(stations);
    }

    private Optional<Station> getOptional(List<Station> stations) {
        if(stations.isEmpty()){
            return Optional.empty();
        }
        return Optional.ofNullable(stations.get(0));
    }

    @Override
    public boolean existByName(String name) {
        final String sql = "SELECT COUNT(*) FROM station WHERE name = :name";
        SqlParameterSource parameters = new MapSqlParameterSource("name", name);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, parameters, Integer.class);
        return count != 0;
    }
}
