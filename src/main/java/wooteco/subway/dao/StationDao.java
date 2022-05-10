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
    private final StationMapper stationMapper;

    public StationDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.stationMapper = new StationMapper();
    }

    public Station save(final Station station) {
        final String sql = "insert into Station (name) values (?)";
        jdbcTemplate.update(sql, station.getName());
        return includeIdIn(station);
    }

    private Station includeIdIn(final Station station) {
        final String sql = "select max(id) from Station";
        final Long id = jdbcTemplate.queryForObject(sql, Long.class);
        return new Station(id, station.getName());
    }

    public int counts(final String name) {
        final String sql = String.format("select count(*) from Station where name = '%s'", name);
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public List<Station> findAll() {
        final String sql = "select * from Station";
        return jdbcTemplate.query(sql, new StationMapper());
    }

    public void deleteById(final Long id) {
        final String sql = "delete from Station where id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Station findById(final Long id) {
        final String sql = "select * from Station where id = ?";
        return jdbcTemplate.queryForObject(sql, stationMapper, id);
    }

    public List<Station> findStationsById(final Set<Long> ids) {
        final String sql = String.format("select * from Station where id in (%s)",
                ids.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")));
        return jdbcTemplate.query(sql, new StationMapper());
    }

    private static final class StationMapper implements RowMapper<Station> {
        public Station mapRow(final ResultSet rs, final int rowCnt) throws SQLException {
            return new Station(rs.getLong("id"), rs.getString("name"));
        }
    }
}
