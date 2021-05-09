package wooteco.subway.station.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.Station;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class JdbcStationDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Station save(Station station) {
        Map<String, String> map = new HashMap<>();
        map.put("name", station.getName());

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("STATION").usingGeneratedKeyColumns("id");
        final long id = jdbcInsert.executeAndReturnKey(map).longValue();

        return Station.of(id, station.getName());
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, stationRowMapper());
    }

    @Override
    public boolean existByName(String name) {
        String sql = "SELECT count(id) FROM station WHERE name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, int.class, name);

        return count >= 1;
    }

    @Override
    public void remove(Long id) {
        String sql = "DELETE FROM station WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void removeAll() {
        String sql = "DELETE FROM station";
        jdbcTemplate.update(sql);
    }

    @Override
    public boolean existById(Long id) {
        String sql = "SELECT count(id) FROM station WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, int.class, id);

        return count >= 1;
    }

    private RowMapper<Station> stationRowMapper() {
        return (rs, rowNum) -> {
            Long foundId = rs.getLong("id");
            final String name = rs.getString("name");
            return Station.of(foundId, name);
        };
    }
}
