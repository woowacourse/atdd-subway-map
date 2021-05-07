package wooteco.subway.station;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class StationDao {

    public static final RowMapper<StationEntity> STATION_ROW_MAPPER = (resultSet, rowNum) -> new StationEntity(
        resultSet.getLong("id"), resultSet.getString("name"));

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public StationEntity save(StationEntity stationEntity) {
        String sql = "INSERT INTO station (name) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, stationEntity.getName());
            return ps;
        }, keyHolder);

        return new StationEntity(Objects.requireNonNull(keyHolder.getKey()).longValue(), stationEntity.getName());
    }

    public List<StationEntity> findAll() {
        String sql = "SELECT * FROM station";

        return jdbcTemplate.query(sql, STATION_ROW_MAPPER);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM station WHERE id = (?)";
        jdbcTemplate.update(sql, id);
    }

    public boolean hasStationWithName(String name) {
        String sql = "SELECT COUNT(*) FROM station WHERE name = (?)";

        return jdbcTemplate.queryForObject(sql, Integer.class, name) > 0;
    }

    public boolean hasStationWithId(Long id) {
        String sql = "SELECT COUNT(*) FROM station WHERE id = (?)";

        return jdbcTemplate.queryForObject(sql, Integer.class, id) > 0;
    }
}
