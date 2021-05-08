package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) ->
        new Station(
            resultSet.getLong("id"),
            resultSet.getString("name"));


    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Station station) {
        String sql = "INSERT INTO STATION (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public Optional<Station> findById(Long id) {
        String query = "SELECT * FROM STATION WHERE id = ?";
        Station result = DataAccessUtils.singleResult(
            jdbcTemplate.query(query, stationRowMapper, id)
        );
        return Optional.ofNullable(result);
    }

    public List<Station> findAll() {
        String query = "SELECT * FROM STATION";
        return jdbcTemplate.query(query, stationRowMapper);
    }

    public long deleteById(Long id) {
        String query = "DELETE FROM STATION WHERE id = ?";
        return jdbcTemplate.update(query, id);
    }
}
