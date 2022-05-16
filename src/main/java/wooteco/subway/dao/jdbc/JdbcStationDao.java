package wooteco.subway.dao.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class JdbcStationDao implements StationDao {

    private static final RowMapper<Station> STATION_ROW_MAPPER = (rs, rowNum) -> new Station(
            rs.getLong("id"),
            rs.getString("name")
    );
    private final JdbcTemplate jdbcTemplate;

    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Station create(Station station) {
        String sql = "INSERT INTO station (name) VALUES(?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, station.getName());
            return statement;
        }, keyHolder);
        return new Station(keyHolder.getKey().longValue(), station.getName());
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, STATION_ROW_MAPPER);
    }

    @Override
    public Station findById(Long id) {
        String sql = "SELECT * FROM station WHERE id=?";
        return jdbcTemplate.queryForObject(sql, STATION_ROW_MAPPER, id);
    }

    @Override
    public Station findByName(String name) {
        String sql = "SELECT * FROM station WHERE name=?";
        return jdbcTemplate.queryForObject(sql, STATION_ROW_MAPPER, name);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM station WHERE id=?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existByName(String name) {
        String sql = "SELECT exists (select * from station where name =?)";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count != 0;
    }

    @Override
    public boolean existById(Long id) {
        String sql = "SELECT exists (select * from station where id =?)";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != 0;
    }
}
