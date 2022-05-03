package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) ->
            new Station(
                    resultSet.getLong("id"),
                    resultSet.getString("name")
            );


    public Station save(String name) {
        final String sql = "INSERT INTO STATION(name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, name);
            return ps;
        }, keyHolder);

        return new Station(keyHolder.getKey().longValue(), name);
    }

    public List<Station> findAll() {
        final String sql = "SELECT * FROM STATION";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public void delete(Long id) {
        final String sql = "DELETE FROM STATION WHERE id=?";
        int count = jdbcTemplate.update(sql, id);
        if (count == 0) {
            new IllegalArgumentException("존재하지 않는 지하철역입니다.");
        }
    }

    public void validate(String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM STATION WHERE name=?)";
        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, name);
        if (result) {
            throw new IllegalArgumentException("지하철 이름이 중복될 수 없습니다.");
        }
    }

}
