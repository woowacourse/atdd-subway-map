package wooteco.subway.dao;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        String sql = "INSERT INTO STATION (name) VALUES (?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        long stationId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Station(stationId, station.getName());
    }

    public List<Station> findAll() {
        String sql = "SELECT id, name FROM STATION";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public Optional<Station> findById(Long id) {
        String sql = "SELECT id, name FROM STATION WHERE id = ?";
        List<Station> stations = jdbcTemplate.query(sql, stationRowMapper, id);
        return Optional.ofNullable(DataAccessUtils.singleResult(stations));
    }

    public Optional<Station> findByName(String name) {
        String sql = "SELECT id, name FROM STATION WHERE name = ?";
        List<Station> stations = jdbcTemplate.query(sql, stationRowMapper, name);
        return Optional.ofNullable(DataAccessUtils.singleResult(stations));
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM STATION WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
