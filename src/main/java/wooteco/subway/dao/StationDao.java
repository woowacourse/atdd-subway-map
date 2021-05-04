package wooteco.subway.dao;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class StationDao {
    private JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) ->
            new Station(
                    resultSet.getLong("id"),
                    resultSet.getString("name"));

    public Station save(String name) {
        String sql = "INSERT INTO STATION (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, name);
            return ps;
        }, keyHolder);
        Long createdStationId = keyHolder.getKey().longValue();
        return new Station(createdStationId, name);
    }

    public Optional<Station> findByName(String name) {
        String query = "SELECT * FROM STATION WHERE name = ?";
        Station result = DataAccessUtils.singleResult(
                jdbcTemplate.query(query, stationRowMapper, name)
        );
        return Optional.ofNullable(result);
    }

    public List<Station> findAll() {
        String query = "SELECT * FROM STATION";
        return jdbcTemplate.query(query, stationRowMapper);
    }

    public Long deleteById(Long stationId) {
        String query = "DELETE FROM STATION WHERE id = ?";
        return (long) jdbcTemplate.update(query, stationId);
    }
}
