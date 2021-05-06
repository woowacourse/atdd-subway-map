package wooteco.subway.station;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<StationResponse> stationRowMapper = (resultSet, rowNum) -> new StationResponse(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(String stationName) {
        String sql = "INSERT INTO STATION (name) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, stationName);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<StationResponse> findAll() {
        String sql = "SELECT * FROM STATION";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public void delete(long stationId) {
        String sql = "DELETE FROM STATION WHERE id=?";
        jdbcTemplate.update(sql, stationId);
    }

    public StationResponse findById(Long id) {
        String sql = "SELECT * FROM STATION WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, stationRowMapper, id);
    }
}
