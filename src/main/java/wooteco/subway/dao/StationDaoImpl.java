package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DataNotFoundException;

@Repository
public class StationDaoImpl implements StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Station> stationRowMapper() {
        return (rs, rowNum) ->
                new Station(rs.getLong("id"), rs.getString("name"));
    }

    @Override
    public Station save(Station station) {
        final String sql = "insert into station (name) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);
        return new Station(keyHolder.getKey().longValue(), station.getName());
    }

    @Override
    public List<Station> findAll() {
        final String sql = "select * from station";
        return jdbcTemplate.query(sql, stationRowMapper());
    }

    @Override
    public Station findById(long id) {
        try {
            final String sql = "select * from station where id = (?)";
            return jdbcTemplate.queryForObject(sql, stationRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("존재하지 않는 역입니다.");
        }
    }

    @Override
    public int deleteStation(long id) {
        final String sql = "delete from station where id = (?)";
        return jdbcTemplate.update(sql, id);
    }
}
