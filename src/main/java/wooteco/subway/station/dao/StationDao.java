package wooteco.subway.station.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.domain.Station;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final RowMapper<Station> rowMapper;

    public StationDao(final JdbcTemplate jdbcTemplate, final DataSource source) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(source)
            .withTableName("STATION")
            .usingGeneratedKeyColumns("id");
        this.rowMapper = (rs, rowNum) -> {
            final Long foundId = rs.getLong("id");
            final String name = rs.getString("name");
            return new Station(foundId, name);
        };
    }

    public Station save(final Station station) {
        Map<String, String> params = new HashMap<>();
        params.put("name", station.getName());

        Long id = jdbcInsert.executeAndReturnKey(params).longValue();

        return new Station(id, station.getName());
    }

    public Optional<Station> showStation(final Long stationId) {
        String statement = "SELECT * FROM STATION WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(statement, rowMapper, stationId));
    }

    public List<Station> showAll() {
        String statement = "SELECT * FROM STATION";
        return jdbcTemplate.query(statement, rowMapper);
    }

    public int countByName(final String name) {
        String statement = "SELECT * FROM STATION WHERE name = ?";
        List<Station> stations =  jdbcTemplate.query(statement, rowMapper, name);
        return stations.size();
    }

    public int delete(final long id) {
        String statement = "DELETE FROM station WHERE id = ?";
        return jdbcTemplate.update(statement, id);
    }
}