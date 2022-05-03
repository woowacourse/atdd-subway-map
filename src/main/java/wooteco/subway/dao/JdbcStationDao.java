package wooteco.subway.dao;

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

    public JdbcStationDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Station> stationRowMapper = (rs, rowNum) -> new Station(
                    rs.getLong("id"),
                    rs.getString("name")
    );

    @Override
    public Station save(final Station station) {
        final String sql = "insert into STATION (name) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Station(id, station.getName());
    }

    @Override
    public List<Station> findAll() {
        final String sql = "select * from STATION";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    @Override
    public boolean existByName(final String name) {
        return false;
    }

    @Override
    public int delete(final Long stationId) {
        return 0;
    }
}
