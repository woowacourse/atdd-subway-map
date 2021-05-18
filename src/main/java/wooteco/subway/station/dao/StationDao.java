package wooteco.subway.station.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

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

    @Transactional(readOnly = true)
    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Station(
                rs.getLong("id"),
                rs.getString("name")
        ));
    }

    public int delete(Long id) {
        String sql = "DELETE FROM station Where id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Transactional(readOnly = true)
    public int countStationByName(String name) {
        String sql = "SELECT count(*) FROM station WHERE name = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, name);
    }

    @Transactional(readOnly = true)
    public Optional<Station> findById(Long id) {
        try {
            String sql = "SELECT * FROM station WHERE id = ?";
            return Optional.of(
                    jdbcTemplate.queryForObject(sql,
                            (rs, rowNum) -> new Station(
                                    rs.getLong("id"),
                                    rs.getString("name")),
                            id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
