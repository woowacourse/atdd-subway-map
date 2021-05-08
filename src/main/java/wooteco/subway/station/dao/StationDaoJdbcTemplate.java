package wooteco.subway.station.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.Station;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class StationDaoJdbcTemplate implements StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final RowMapper<Station> stationRowMapper;

    public StationDaoJdbcTemplate(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;

        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("STATION").usingGeneratedKeyColumns("id");

        this.stationRowMapper = (rs, rowNum) -> {
            Long foundId = rs.getLong("id");
            final String name = rs.getString("name");
            return Station.of(foundId, name);
        };
    }

    @Override
    public Station save(Station station) {
        Map<String, String> map = new HashMap<>();
        map.put("name", station.getName());
        final long id = jdbcInsert.executeAndReturnKey(map).longValue();
        return Station.of(id, station.getName());
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    @Override
    public Optional<Station> findStationByName(String name) {
        String sql = "SELECT * FROM station WHERE name = ?";
        return jdbcTemplate.query(sql, stationRowMapper, name).stream().findAny();
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
}
