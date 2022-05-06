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
public class StationDaoImpl implements StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Station> actorRowMapper = (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    @Override
    public Station save(Station station) {
        String sql = "insert into station (name) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Station(id, station.getName());
    }

    @Override
    public List<Station> findAll() {
        String sql = "select * from station";
        return jdbcTemplate.query(sql, actorRowMapper);
    }

    @Override
    public int deleteById(Long id) {
        String sql = "delete from station where id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean exists(Station station) {
        String sql = "select exists (select name from station where name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, station.getName());
    }

    @Override
    public boolean exists(final Long id) {
        String sql = "select exists (select id from station where id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }
}
