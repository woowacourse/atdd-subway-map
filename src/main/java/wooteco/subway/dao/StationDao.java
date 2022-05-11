package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class StationDao {

    private static final String TABLE_NAME = "STATION";
    private static final String KEY_NAME = "id";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.insertActor = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(KEY_NAME);
    }

    public Station save(Station station) {
        Long id = insertActor.executeAndReturnKey(Map.of("name", station.getName())).longValue();
        return findById(id).get();
    }

    public boolean existsByName(String name) {
        String sql = "select count(*) from STATION where name = :name";
        return 0 != jdbcTemplate.queryForObject(sql, Map.of("name", name), Integer.class);
    }

    public Optional<Station> findById(Long id) {
        String sql = "select * from STATION where id = :id";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, Map.of("id", id), generateMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Station> findAll() {
        String sql = "select * from STATION";
        return jdbcTemplate.query(sql, generateMapper());
    }

    private RowMapper<Station> generateMapper() {
        return (resultSet, rowNum) ->
                new Station(
                        resultSet.getLong("id"),
                        resultSet.getString("name")
                );
    }

    public void deleteById(Long id) {
        String sql = "delete from STATION where id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }
}
