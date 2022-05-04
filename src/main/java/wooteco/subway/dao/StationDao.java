package wooteco.subway.dao;

import static java.lang.Boolean.*;

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

    private static RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    public Station save(Station station) {
        String sql = "SELECT EXISTS (SELECT * FROM station WHERE name = ? LIMIT 1)";

        final Boolean isExist = jdbcTemplate.queryForObject(sql, Boolean.class, station.getName());
        if (TRUE.equals(isExist)) {
            throw new IllegalArgumentException("중복된 지하철 역이 존재합니다.");
        }

        String saveSql = "INSERT INTO station (name) values (?)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            final PreparedStatement statement = connection.prepareStatement(saveSql, new String[]{"id"});
            statement.setString(1, station.getName());
            return statement;
        }, keyHolder);

        return new Station(keyHolder.getKey().longValue(), station.getName());
    }


    public List<Station> findAll() {
        String sql = "SELECT * FROM station";

        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public void deleteById(Long id) {
        String deleteSql = "DELETE FROM station WHERE id = ?";

        jdbcTemplate.update(deleteSql, id);
    }
}
