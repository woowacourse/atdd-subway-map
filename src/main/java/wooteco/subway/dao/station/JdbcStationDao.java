package wooteco.subway.dao.station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.station.Station;

@Repository
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
        String sql = "INSERT INTO station (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((con) -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);
        return new Station(keyHolder.getKey().longValue(), station.getName());
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public Optional<Station> findById(Long id) {
        String sql = "SELECT * FROM station WHERE id = ?";
        return jdbcTemplate.query(sql, mapper, id)
            .stream()
            .findAny();
    }

    @Override
    public boolean doesNotExistName(String name) {
        String sql = "SELECT count(*) FROM station WHERE name = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count == 0;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM station WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
