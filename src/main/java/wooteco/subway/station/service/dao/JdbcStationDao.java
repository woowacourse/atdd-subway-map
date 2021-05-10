package wooteco.subway.station.service.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
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
public class JdbcStationDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final RowMapper<Station> stationRowMapper;
    private final ObjectMapper objectMapper;

    public JdbcStationDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;

        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("STATION").usingGeneratedKeyColumns("id");

        this.stationRowMapper = (rs, rowNum) -> {
            Long foundId = rs.getLong("id");
            final String name = rs.getString("name");
            return new Station(foundId, name);
        };

        objectMapper = new ObjectMapper();
    }

    @Override
    public Station save(Station station) {
        Map<String, String> parameters = objectMapper.convertValue(station, Map.class);

        final long id = jdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Station(id, station.getName());
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    @Override
    public Optional<Station> findStationById(Long id) {
        final String sql = "SELECT * FROM station WHERE id = ?";
        return jdbcTemplate.query(sql, stationRowMapper, id).stream().findAny();
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
