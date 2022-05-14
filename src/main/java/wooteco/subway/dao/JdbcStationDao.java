package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Station;
import wooteco.subway.exception.DataReferenceViolationException;

@Repository
public class JdbcStationDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Station save(Station station) {
        String sql = "insert into station (name) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Station(id, station.getName());
    }

    @Override
    public Optional<Station> findByName(String name) {
        String sql = "select * from station where name = ?";

        try {
            Station station = jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> createStation(rs), name);
            return Optional.ofNullable(station);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Station createStation(ResultSet rs) throws SQLException {
        return new Station(
            rs.getLong("id"),
            rs.getString("name")
        );
    }

    @Override
    public Optional<Station> findById(Long id) {
        String sql = "select * from station where id = ?";

        try {
            Station station = jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> createStation(rs), id);
            return Optional.ofNullable(station);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Station> findAll() {
        String sql = "select * from station";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createStation(rs));
    }

    @Override
    public int deleteById(Long id) {
        String sql = "delete from station where id = ?";
        try {
            return jdbcTemplate.update(sql, id);
        } catch (DataIntegrityViolationException e) {
            throw new DataReferenceViolationException("구간에 할당된 역이 존재하여 삭제할 수 없습니다.");
        }
    }
}
