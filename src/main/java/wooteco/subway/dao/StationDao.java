package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        String sql = "insert into STATION (name) values (?)";
        jdbcTemplate.update(sql, station.getName());

        return createNewObject(station);
    }

    public void checkDuplication(Station station) {
        String sql = String.format("select count(*) from STATION where name = '%s'", station.getName());

        if (jdbcTemplate.queryForObject(sql, Integer.class) > 0) {
            throw new IllegalArgumentException("이미 존재하는 역 이름입니다.");
        }
    }

    public List<Station> findAll() {
        String sql = "select * from STATION";
        return jdbcTemplate.query(sql, new StationMapper());
    }

    public void delete(Long id) {
        String sql = "delete from STATION where id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class StationMapper implements RowMapper<Station> {
        public Station mapRow(ResultSet rs, int rowCnt) throws SQLException {
            return new Station(rs.getLong("id"), rs.getString("name"));
        }
    }

    private Station createNewObject(Station station) {
        String sql2 = "select max(id) from STATION";
        Long id = jdbcTemplate.queryForObject(sql2, Long.class);
        return new Station(id, station.getName());
    }

    public Station findById(Long id) {
        String sql = String.format("select * from STATION where id = %d", id);
        return jdbcTemplate.queryForObject(sql, new StationMapper());
    }

    public List<Station> findByIdIn(Set<Long> ids) {
        List<String> stringIds = ids.stream()
                .map(id -> Long.toString(id))
                .collect(Collectors.toList());

        String sql = String.format("select * from STATION where id in (%s)", String.join(", ", stringIds));
        return jdbcTemplate.query(sql, new StationMapper());
    }
}
