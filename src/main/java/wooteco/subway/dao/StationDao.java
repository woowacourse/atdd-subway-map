package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Station;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private final RowMapper<Station> stationRowMapper = (rs, rowNum) -> {
        final long id = rs.getLong("id");
        final String name = rs.getString("name");
        return new Station(id, name);
    };

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(jdbcTemplate).withTableName("STATION").usingGeneratedKeyColumns("id");
    }

    public Station save(Station station) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", station.getName());

        Long generatedId = insertAction.executeAndReturnKey(params).longValue();
        return new Station(generatedId, station.getName());
    }

    public List<Station> findAll() {
        String sql = "SELECT s.id, s.name FROM STATION s";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public Optional<Station> findById(final Long stationId) {
        String sql = "SELECT s.id, s.name FROM STATION s WHERE s.id = ?";
        final List<Station> stations = jdbcTemplate.query(sql, stationRowMapper, stationId);
        return Optional.ofNullable(DataAccessUtils.singleResult(stations));
    }

    public Optional<Station> findByName(String name) {
        String sql = "SELECT s.id, s.name FROM STATION s WHERE s.name = ?";
        final List<Station> stations = jdbcTemplate.query(sql, stationRowMapper, name);
        return Optional.ofNullable(DataAccessUtils.singleResult(stations));
    }

    public void delete(Long id) {
        String sql = "DELETE FROM STATION s WHERE s.id = ?";
        jdbcTemplate.update(sql, id);
    }
}
