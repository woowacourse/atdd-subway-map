package wooteco.subway.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class StationJdbcDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Station save(Station station) {
        String sql = "INSERT INTO STATION (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Station(generatedId, station.getName());
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT s.id, s.name FROM STATION s";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            final long id = rs.getLong("id");
            final String name = rs.getString("name");
            return new Station(id, name);
        });
    }

    @Override
    public Optional<Station> findById(final Long stationId) {
        String sql = "SELECT s.id, s.name FROM STATION s WHERE s.id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                final String name = rs.getString("name");
                return Optional.of(new Station(stationId, name));
            }, stationId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Station> findByName(String name) {
        String sql = "SELECT s.id, s.name FROM STATION s WHERE s.name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                final long id = rs.getLong("id");
                return Optional.of(new Station(id, name));
            }, name);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM STATION s WHERE s.id = ?";
        jdbcTemplate.update(sql, id);
    }
}
