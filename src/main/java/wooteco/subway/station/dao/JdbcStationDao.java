package wooteco.subway.station.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.station.StationNotFoundException;
import wooteco.subway.station.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class JdbcStationDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Station save(Station station) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO station (name) VALUES (?)";
        String name = station.getName();
        jdbcTemplate.update(con -> {
            PreparedStatement pstmt = con.prepareStatement(query, new String[]{"id"});
            pstmt.setString(1, name);
            return pstmt;
        }, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Station(id, name);
    }

    @Override
    public Station findById(Long id) {
        try {
            String query = "SELECT * FROM station WHERE id = ?";
            return jdbcTemplate.queryForObject(query, stationRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new StationNotFoundException();
        }
    }

    @Override
    public boolean existByName(String name) {
        String query = "SELECT EXISTS (SELECT * FROM station WHERE name = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, name);
    }

    @Override
    public boolean existById(Long id) {
        String query = "SELECT EXISTS (SELECT * FROM station WHERE id = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, id);
    }

    @Override
    public Station findByName(String name) {
        try {
            String query = "SELECT * FROM station WHERE name = ?";
            return jdbcTemplate.queryForObject(query, stationRowMapper(), name);
        } catch (EmptyResultDataAccessException e) {
            throw new StationNotFoundException();
        }
    }

    @Override
    public List<Station> findAll() {
        String query = "SELECT * FROM station";
        return jdbcTemplate.query(query, stationRowMapper());
    }

    private RowMapper<Station> stationRowMapper() {
        return (rs, rowNum) -> new Station(
                rs.getLong("id"),
                rs.getString("name")
        );
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM station WHERE id = ?";
        jdbcTemplate.update(query, id);
    }
}
