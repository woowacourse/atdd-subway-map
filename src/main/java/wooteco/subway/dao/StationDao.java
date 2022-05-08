package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Station;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> new Station(
        resultSet.getLong("id"),
        resultSet.getString("name")
    );

    public Station save(Station station) {
        final String sql = "INSERT INTO station (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[] {"id"});
            preparedStatement.setString(1, station.getName());
            return preparedStatement;
        }, keyHolder);

        return new Station(keyHolder.getKey().longValue(), station.getName());
    }

    public Optional<Station> findByName(String name) {
        final String sql = "SELECT * FROM station WHERE name = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, stationRowMapper, name));
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Station> findById(Long id) {
        final String sql = "SELECT * FROM station WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, stationRowMapper, id));
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Station> findAll() {
        final String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public void delete(Long id) {
        final String sql = "delete from station where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
