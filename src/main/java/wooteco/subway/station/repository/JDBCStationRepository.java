package wooteco.subway.station.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class JDBCStationRepository implements StationRepository {

    private final JdbcTemplate jdbcTemplate;

    public JDBCStationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Station> actorRowMapper = (resultSet, rowNum) ->
            new Station(
                    resultSet.getLong("id"),
                    resultSet.getString("name")
            );

    @Override
    public Station save(Station station) {
        String query = "INSERT INTO station (name) VALUES(?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);
        return findById(keyHolder.getKey().longValue());
    }

    @Override
    public List<Station> findAll() {
        String query = "SELECT * FROM station";
        return this.jdbcTemplate.query(query, actorRowMapper);
    }

    @Override
    public Station findById(Long id) {
        String query = "SELECT * FROM station WHERE id = ?";
        return this.jdbcTemplate.queryForObject(query, actorRowMapper, id);
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM station WHERE id = ?";
        this.jdbcTemplate.update(query, id);
    }
}
