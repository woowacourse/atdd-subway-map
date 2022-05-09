package wooteco.subway.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(final Station station) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "INSERT INTO STATION (name) VALUES (?)";
        jdbcTemplate.update((Connection con) -> {
            PreparedStatement pstmt = con.prepareStatement(sql, new String[]{"id"});
            pstmt.setString(1, station.getName());
            return pstmt;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public Station findById(final Long id) {
        final String sql = "SELECT * FROM STATION WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper(), id);
    }

    public List<Station> findAll() {
        final String sql = "SELECT * FROM STATION";
        return jdbcTemplate.query(sql, rowMapper());
    }

    public void delete(final Long id) {
        final String sql = "DELETE FROM STATION WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private RowMapper<Station> rowMapper() {
        return (resultSet, rowNum) -> new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
    }
}
