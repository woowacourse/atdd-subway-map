package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
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

    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> {
        Station station = new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
        return station;
    };

    public Long save(Station station) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into STATION (name) values (?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, station.getName());
            return preparedStatement;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public List<Station> findAll() {
        String sql = "select * from STATION";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public void deleteById(Long stationId) {
        String sql = "delete from STATION where id = (?)";
        jdbcTemplate.update(sql, stationId);
    }

    public Station findById(Long stationId) {
        String sql = "select * from STATION where id = (?)";
        return jdbcTemplate.queryForObject(sql, stationRowMapper, stationId);
    }

    public boolean existByName(Station station) {
        String sql = "select exists (select * from STATION where name = (?))";
        return jdbcTemplate.queryForObject(sql, Boolean.class, station.getName());
    }

}
