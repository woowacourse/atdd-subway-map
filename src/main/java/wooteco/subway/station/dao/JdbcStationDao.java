package wooteco.subway.station.dao;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.Station;

@Repository
public class JdbcStationDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final StationMapper mapper;

    public JdbcStationDao(JdbcTemplate jdbcTemplate, StationMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public Station save(Station station) {
        String query = "insert into STATION (name) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((con) -> {
            PreparedStatement ps = con.prepareStatement(query, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);
        return new Station(keyHolder.getKey().longValue(), station.getName());
    }

    @Override
    public List<Station> findAll() {
        String query = "select * from STATION";
        return jdbcTemplate.query(query, mapper);
    }

    @Override
    public void deleteById(Long id) {
        String query = "delete from STATION where id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public Station findById(Long id) {
        String query = "select * from STATION where id = ?";
        return jdbcTemplate.queryForObject(query, mapper, id);
    }

    @Override
    public List<Station> findAllByIds(List<Long> ids) {
        String query = String
            .format("select * from STATION where id in (%s)", stringify(ids.size()));
        return jdbcTemplate.query(query, mapper, ids.toArray());
    }

    private String stringify(int size) {
        return String.join(",", Collections.nCopies(size, "?"));
    }
}
