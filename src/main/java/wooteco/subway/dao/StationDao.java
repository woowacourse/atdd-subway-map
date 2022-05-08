package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        String sql = "insert into Station (name) values (?)";
        jdbcTemplate.update(sql, station.getName());

        return includeIdIn(station);
    }

    private Station includeIdIn(Station station) {
        String sql = "select max(id) from Station";
        Long id = jdbcTemplate.queryForObject(sql, Long.class);
        return new Station(id, station.getName());
    }

    public int counts(String name) {
        String sql = String.format("select count(*) from Station where name = '%s'", name);
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public List<Station> findAll() {
        String sql = "select * from Station";
        return jdbcTemplate.query(sql, new StationMapper());
    }

    public void deleteById(Long id) {
        String sql = "delete from Station where id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static final class StationMapper implements RowMapper {
        public Station mapRow(ResultSet rs, int rowCnt) throws SQLException {
            return new Station(rs.getLong("id"), rs.getString("name"));
        }

    }
}
