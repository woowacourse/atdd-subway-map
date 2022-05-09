package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.StationEntity;

@Repository
public class JdbcStationDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public StationEntity save(StationEntity station) {
        String sql = "insert into station (name) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new StationEntity(id, station.getName());
    }

    @Override
    public Optional<StationEntity> findByName(String name) {
        String sql = "select * from station where name = ?";

        try {
            StationEntity station = jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> createStation(rs), name);
            return Optional.ofNullable(station);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private StationEntity createStation(ResultSet rs) throws SQLException {
        return new StationEntity(
            rs.getLong("id"),
            rs.getString("name")
        );
    }

    @Override
    public Optional<StationEntity> findById(Long id) {
        String sql = "select * from station where id = ?";

        try {
            StationEntity station = jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> createStation(rs), id);
            return Optional.ofNullable(station);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<StationEntity> findAll() {
        String sql = "select * from station";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createStation(rs));
    }

    @Override
    public int deleteById(Long id) {
        String sql = "delete from station where id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
