package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class StationDao {

    private static final RowMapper<Station> STATION_ROW_MAPPER = (rs, rowNum) -> new Station(
            rs.getLong("id"),
            rs.getString("name")
    );
    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        var sql = "INSERT INTO station (name) VALUES(?)";
        var keyHolder = new GeneratedKeyHolder();
        save(station, sql, keyHolder);
        return new Station(keyHolder.getKey().longValue(), station.getName());
    }

    private void save(Station station, String sql, KeyHolder keyHolder) {
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
                statement.setString(1, station.getName());
                return statement;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("[ERROR] 이미 존재하는 역 이름 입니다.");
        }
    }

    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, STATION_ROW_MAPPER);
    }

    public Station findByName(String name) {
        String sql = "SELECT * FROM station WHERE name=?";
        return jdbcTemplate.queryForObject(sql, STATION_ROW_MAPPER, name);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM station WHERE id=?";
        jdbcTemplate.update(sql, id);
    }
}
