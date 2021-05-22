package wooteco.subway.station.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcStationDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
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
        return new Station(keyHolder.getKey().longValue(), station.getName());
    }

    @Override
    public List<Station> findAll() {
        String query = "SELECT * FROM station";
        return this.jdbcTemplate.query(query, actorRowMapper);
    }

    @Override
    public Optional<Station> findById(Long id) {
        String query = "SELECT * FROM station WHERE id = ?";
        return this.jdbcTemplate.query(query, actorRowMapper, id)
                .stream()
                .findAny();
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM STATION WHERE id = ?";
        this.jdbcTemplate.update(query, id);
    }

    @Override
    public Optional<Station> findStationByName(String name) {
        String query = "SELECT * FROM station WHERE name = ?";
        return this.jdbcTemplate.query(query, actorRowMapper, name)
                .stream()
                .findAny();
    }

    @Override
    public List<Station> findByIds(List<Long> ids) {
        String query = "SELECT * FROM station";
        List<Station> stations = this.jdbcTemplate.query(query, actorRowMapper);

        List<Station> findByIds = new ArrayList<>();

        for(Long id : ids) {
            Station findById = stations.stream()
                    .filter(station -> station.equalId(id))
                    .findFirst()
                    .get();

            findByIds.add(findById);
        }

        return findByIds;
    }
}
