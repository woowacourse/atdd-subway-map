package wooteco.subway.station.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationRepository;

@Repository
public class StationDao implements StationRepository {

    private final JdbcTemplate jdbcTemplate;
    private final KeyHolder keyHolder = new GeneratedKeyHolder();
    private final RowMapper<Station> rowMapper = (rs, rn) ->
            new Station(
                    rs.getLong("id"),
                    rs.getString("name")
            );

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Station save(final Station station) {
        final String sql = "INSERT INTO STATION (name) VALUES (?)";
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);
        return new Station(keyHolder.getKey().longValue(), station.getName());
    }

    @Override
    public Optional<Station> findByName(final String name) {
        final String sql = "SELECT * FROM STATION WHERE name = ?";
        final List<Station> result = jdbcTemplate.query(sql, rowMapper, name);
        return optionalOf(result);
    }

    @Override
    public Optional<Station> findById(Long id) {
        final String sql = "SELECT * FROM STATION WHERE id = ?";
        final List<Station> result = jdbcTemplate.query(sql, rowMapper, id);
        return optionalOf(result);
    }

    private Optional<Station> optionalOf(final List<Station> result) {
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(result.get(0));
    }

    @Override
    public List<Station> findAll() {
        final String sql = "SELECT * FROM STATION";
        return jdbcTemplate.query(
                sql,
                rowMapper
        );
    }

    @Override
    public void delete(final Long id) {
        final String sql = "DELETE FROM STATION WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
