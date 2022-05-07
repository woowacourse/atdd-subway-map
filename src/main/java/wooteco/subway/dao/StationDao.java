package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Station;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(final Station station) {
        final String sql = "insert into Station (name) values (?)";
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, station.getName());
            return statement;
        }, keyHolder);

        return new Station(
            Objects.requireNonNull(keyHolder.getKey()).longValue(),
            station.getName()
        );
    }

    public List<Station> findAll() {
        final String sql = "select id, name from Station";
        return jdbcTemplate.query(sql, rowMapper());
    }

    public Optional<Station> findById(final Long id) {
        final String sql = "select id, name from Station where id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Station> findByName(final String name) {
        final String sql = "select id, name from Station where name = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper(), name));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private static RowMapper<Station> rowMapper() {
        return (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
        );
    }

    public void deleteById(final Long id) {
        final String sql = "delete from Station where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
