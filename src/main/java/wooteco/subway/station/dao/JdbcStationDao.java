package wooteco.subway.station.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class JdbcStationDao implements StationDao {
    private static final String CREATE = "INSERT INTO station (name) VALUES (?)";
    private static final String READ_ALL = "SELECT * FROM station";
    private static final String READ_BY_ID = "SELECT * FROM station WHERE id = ?";
    private static final String COUNT_BY_ID = "SELECT count(id) FROM station WHERE id = ?";
    private static final String COUNT_BY_NAME = "SELECT count(id) FROM station WHERE name = ?";
    private static final String DELETE_BY_ID = "DELETE FROM station WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Station create(Station station) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(CREATE, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        return Station.create(keyHolder.getKey().longValue(), station.getName());
    }

    @Override
    public List<Station> findAll() {
        return this.jdbcTemplate.query(READ_ALL, stationRowMapper());
    }

    @Override
    public Station findById(Long id) {
        return this.jdbcTemplate.queryForObject(READ_BY_ID, stationRowMapper(), id);
    }

    @Override
    public boolean existById(Long id) {
        Integer count = this.jdbcTemplate.queryForObject(COUNT_BY_ID, int.class, id);
        return count >= 1;
    }

    @Override
    public boolean existByName(String name) {
        Integer count = this.jdbcTemplate.queryForObject(COUNT_BY_NAME, int.class, name);
        return count >= 1;
    }

    @Override
    public void remove(Long id) {
        this.jdbcTemplate.update(DELETE_BY_ID, id);
    }

    private RowMapper<Station> stationRowMapper() {
        return (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            return Station.create(id, name);
        };
    }

}
