package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Station station) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO station (name) VALUES (?)";

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Station(
                rs.getLong("id"),
                rs.getString("name")
        ));
    }

    public void delete(Long id) {
        String sql = "DELETE FROM station WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
