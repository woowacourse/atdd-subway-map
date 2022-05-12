package wooteco.subway.dao.station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class JdbcStationDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationRowMapper = (rs, rowNum) -> new Station(
            rs.getLong("id"),
            rs.getString("name")
    );

    public JdbcStationDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long save(final Station station) {
        final String sql = "insert into STATION (name) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public List<Station> findAll() {
        final String sql = "select * from STATION";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    @Override
    public Station findById(final Long id) {
        final String sql = "select * from STATION where id = ?";
        return jdbcTemplate.queryForObject(sql, stationRowMapper, id);
    }

    @Override
    public boolean existByName(final String name) {
        final String sql = "select exists (select * from STATION where name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }

    @Override
    public boolean existById(final Long id) {
        final String sql = "select exists (select * from STATION where id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    @Override
    public int delete(final Long stationId) {
        final String sql = "delete from STATION where id = ?";
        return jdbcTemplate.update(sql, stationId);
    }
}
