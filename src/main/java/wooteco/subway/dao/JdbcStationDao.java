package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class JdbcStationDao implements StationDao {

    private static final String TABLE_NAME = "STATION";
    private static final String KEY_NAME = "id";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.insertActor = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(KEY_NAME);
    }

    @Override
    public Station save(Station station) {
        Long id = insertActor.executeAndReturnKey(Map.of("name", station.getName())).longValue();
        return findById(id);
    }

    @Override
    public Station findById(Long id) {
        String sql = "select * from STATION where id = :id";
        return jdbcTemplate.queryForObject(sql, Map.of("id", id), generateMapper());
    }

    @Override
    public List<Station> findAll() {
        String sql = "select * from STATION";
        return jdbcTemplate.query(sql, generateMapper());
    }

    @Override
    public List<Station> findByIdIn(List<Long> ids) {
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        String sql = "select * from STATION where id in (:ids)";
        return jdbcTemplate.query(sql, parameters, generateMapper());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from STATION where id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }

    private RowMapper<Station> generateMapper() {
        return (resultSet, rowNum) ->
                new Station(
                        resultSet.getLong("id"),
                        resultSet.getString("name")
                );
    }
}
