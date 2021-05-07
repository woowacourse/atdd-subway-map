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

    public Long create(String name) {
        String sql = "INSERT INTO station (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, name);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public Optional<Station> findByName(String name) {
        String query = "SELECT * FROM station WHERE name = ?";
        Station result = DataAccessUtils.singleResult(
                jdbcTemplate.query(query, stationRowMapper, name)
        );
        return Optional.ofNullable(result);
    }

    public Optional<Station> findById(Long stationId) {
        String query = "SELECT * FROM station WHERE id = ?";
        Station result = DataAccessUtils.singleResult(
                jdbcTemplate.query(query, (resultSet, rowNum) ->
                                new Station(
                                        resultSet.getLong("id"),
                                        resultSet.getString("name")),
                        stationId));
        return Optional.ofNullable(result);
    }

    public List<Station> findAll() {
        String query = "SELECT * FROM station";
        return jdbcTemplate.query(query, stationRowMapper);
    }

    private RowMapper<Station> stationRowMapper = (resultSet, rowNum) ->
            new Station(
                    resultSet.getLong("id"),
                    resultSet.getString("name"));

    public int edit(Long stationId, String name) {
        String query = "UPDATE station SET name = ? WHERE id = ?";
        return jdbcTemplate.update(query, name, stationId);
    }

    public int deleteById(Long stationId) {
        String query = "DELETE FROM station WHERE id = ?";
        return jdbcTemplate.update(query, stationId);
    }
}
