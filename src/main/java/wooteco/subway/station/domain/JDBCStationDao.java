package wooteco.subway.station.domain;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class JDBCStationDao implements StationDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationsMapper;

    public JDBCStationDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.stationsMapper = (rs, rowNum) -> new Station(
                rs.getLong("id"),
                rs.getString("name"));
    }

    @Override
    public Station save(final Station station) {
        String sql = "INSERT INTO station(name) values(?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.name());
            return ps;
        }, keyHolder);
        return new Station(keyHolder.getKey().longValue(), station.name());
    }

    @Override
    public List<Station> findAll() {
        return jdbcTemplate.query("select * from station order by id desc",
                stationsMapper);
    }

    @Override
    public Optional<Station> findById(final Long id) {
        List<Station> stations = jdbcTemplate.query("select * from station where id = ?",
                stationsMapper,
                id);

        return Optional.ofNullable(DataAccessUtils.singleResult(stations));
    }

    @Override
    public Optional<Station> findByName(final String name) {
        List<Station> stations = jdbcTemplate.query("select * from station where name = ?",
                stationsMapper,
                name);

        return Optional.ofNullable(DataAccessUtils.singleResult(stations));
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("디비 전체 삭제는 불가능!");
    }

    @Override
    public void delete(final Long id) {
        jdbcTemplate.update("delete from station where id = ?", id);
    }
}
