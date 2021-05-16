package wooteco.subway.dao.station;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcStationDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Station save(Station station) {
        String sql = "INSERT INTO station (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, station.getName());
            return preparedStatement;
        }, keyHolder);
        return Station.of(keyHolder.getKey().longValue(), station.getName());
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, getRowMapper());
    }

    @Override
    public Optional<Station> findById(Long id) {
        String sql = "SELECT * FROM station WHERE id = ?";
        try {
            return Optional.ofNullable((Station) jdbcTemplate.queryForObject(sql, getRowMapper(), id));
        }
        catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Station> findByName(String name) {
        String sql = "SELECT * FROM station WHERE name = ?";
        try {
            return Optional.ofNullable((Station) jdbcTemplate.queryForObject(sql, getRowMapper(), name));
        }
        catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM station WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private RowMapper getRowMapper() {
        return (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            return Station.of(id, name);
        };
    }
}
