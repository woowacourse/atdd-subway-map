package wooteco.subway.station;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
public class StationDao {

    public static final RowMapper<Station> STATION_ROW_MAPPER = (resultSet, rowNum) -> new Station(resultSet.getLong("id"), resultSet.getString("name"));

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        String sql = "INSERT INTO station (name) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        return new Station(Objects.requireNonNull(keyHolder.getKey()).longValue(), station.getName());
    }

    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, STATION_ROW_MAPPER);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM station WHERE id = (?)";
        int updatedRowCount = jdbcTemplate.update(sql, id);

        if (updatedRowCount == 0) {
            throw new IllegalArgumentException("존재하지 않는 id 입니다.");
        }
    }

    public int count(String name) {
        String sql = "SELECT count(*) FROM station WHERE `name` = (?)";
        return jdbcTemplate.queryForObject(sql, Integer.class, name);
    }

    public int count(Long id) {
        String sql = "SELECT count(*) FROM station WHERE id = (?)";
        return jdbcTemplate.queryForObject(sql, Integer.class, id);
    }
}
