package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.station.Station;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station insert(String name) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO station (name) VALUES (?)";
        jdbcTemplate.update((Connection con) -> {
            PreparedStatement pstmt = con.prepareStatement(
                    query,
                    new String[]{"id"});
            pstmt.setString(1, name);
            return pstmt;
        }, keyHolder);

        final long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Station(id, name);
    }

    public List<Station> findAll() {
        String query = "SELECT * FROM station";
        return jdbcTemplate.query(query,
                (resultSet, rowNum) -> new Station(
                        resultSet.getLong("id"),
                        resultSet.getString("name")
                )
        );
    }

    public Optional<Station> findByName(String name) {
        String query = "SELECT * FROM station WHERE name=?";
        return jdbcTemplate.query(query,
                (resultSet, rowNum) -> new Station(
                        resultSet.getLong("id"),
                        resultSet.getString("name")
                ),
                name)
                .stream()
                .findAny();
    }

    public void deleteById(Long id) {
        String query = "DELETE FROM station WHERE id = ?";
        jdbcTemplate.update(query, id);
    }
}
