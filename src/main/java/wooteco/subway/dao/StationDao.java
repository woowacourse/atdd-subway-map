package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Station;

@Repository
public class StationDao {
    private static final String NO_ID_STATION_ERROR_MESSAGE = "해당 아이디의 역이 없습니다.";

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        final String sql = "INSERT INTO station (name) VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[] {"id"});
            preparedStatement.setString(1, station.getName());
            return preparedStatement;
        }, keyHolder);

        return new Station(keyHolder.getKey().longValue(), station.getName());
    }

    public Station findByName(String name) {
        final String sql = "SELECT * FROM station WHERE name = ?;";
        List<Station> stations = jdbcTemplate.query(sql, (rs, rowNum) -> new Station(
                rs.getLong("id"),
                rs.getString("name"))
            , name);
        if (stations.isEmpty()) {
            throw new NoSuchElementException(NO_ID_STATION_ERROR_MESSAGE);
        }
        return stations.get(0);
    }

    public List<Station> findAll() {
        final String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Station(
            rs.getLong("id"),
            rs.getString("name")
        ));
    }

    public void delete(Long id) {
        final String sql = "delete from station where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
