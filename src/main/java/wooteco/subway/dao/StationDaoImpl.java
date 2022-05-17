package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
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

    @Override
    public Optional<Station> findById(Long id) {
        final String sql = "SELECT id, name FROM STATION WHERE id = ?";

        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, stationMapper(), id));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Station save(Station station) {
        final String sql = "INSERT INTO STATION (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);
        long id = keyHolder.getKey().longValue();

        return new Station(id, station.getName());
    }

    @Override
    public List<Station> findAll() {
        final String sql = "SELECT id, name FROM STATION";
        return jdbcTemplate.query(sql, stationMapper());
    }

    private RowMapper<Station> stationMapper() {
        return (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
        );
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM station WHERE id = ?";
        int updateSize = jdbcTemplate.update(sql, id);
        return updateSize != 0;
    }
}
