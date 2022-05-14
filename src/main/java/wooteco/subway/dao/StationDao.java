package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.SubwayException;
import wooteco.subway.domain.Station;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> new Station(resultSet.getLong("id"),
            resultSet.getString("name"));

    public Station save(Station station) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into station (name) values (?)";

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, station.getName());
                return ps;
            }, keyHolder);
            long insertedId = keyHolder.getKey().longValue();

            return new Station(insertedId, station.getName());
        }
        catch (DuplicateKeyException e) {
            throw new SubwayException("[ERROR] 중복된 이름으로 추가할 수 없습니다.");
        }
    }

    public List<Station> findAll() {
        String sql = "select id, name from station";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public void deleteById(Long id) {
        String sql = "delete from station where id = ?";
        this.jdbcTemplate.update(sql,id);
    }

    public Station findById(Long id) {
        String sql = "select id, name from station where id = ?";
        return jdbcTemplate.queryForObject(sql, stationRowMapper, id);
    }

    public boolean isValidId(Long id) {
        String sql = "select count(*) from station where id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, id) > 0;
    }
}
