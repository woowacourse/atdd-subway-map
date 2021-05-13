package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class StationH2Dao implements StationRepository {
    private final JdbcTemplate jdbcTemplate;

    public StationH2Dao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Station> stationRowMapper = (rs, rn) -> {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Station(id, name);
    };

    @Override
    public Station save(Station station) {
        String query = "INSERT INTO STATION (name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        this.jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        return this.findById(keyHolder.getKey().longValue());
    }

    @Override
    public Station findById(long id) {
        String query = "SELECT * FROM STATION WHERE id = ?";
        return this.jdbcTemplate.queryForObject(query, stationRowMapper, id);
    }

    @Override
    public List<Station> findAll() {
        String query = "SELECT * FROM STATION";
        return this.jdbcTemplate.query(query, stationRowMapper);
    }

    @Override
    public Optional<Station> findByName(String name) {
        String query = "SELECT * FROM STATION WHERE name = ?";

        return this.jdbcTemplate.query(query, (rs) -> {
            if (rs.next()) {
                long id = rs.getLong("id");
                String stationName = rs.getString("name");
                return Optional.of(new Station(id, stationName));
            }
            return Optional.empty();
        }, name);
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM STATION WHERE id = ?";
        jdbcTemplate.update(query, id);
    }
}
