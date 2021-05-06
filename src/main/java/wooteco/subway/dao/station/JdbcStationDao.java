package wooteco.subway.dao.station;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.station.Station;

@Repository
@Qualifier("jdbc")
public class JdbcStationDao implements StationDao {

    private static final RowMapper<Station> mapper = (rs, rowNum) -> {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Station(id, name);
    };

    private final JdbcTemplate jdbcTemplate;

    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
}
