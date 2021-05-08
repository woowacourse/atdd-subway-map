package wooteco.subway.station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

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
        return jdbcTemplate.query(sql, rowMapper, name).stream()
                .findFirst();
    }

    // query()는 null 반환이 아닌데, Optional로 포장할 이유가 있을까에 대한 고민
    public List<Station> findByName1(final String name) {
        final String sql = "SELECT * FROM STATION WHERE name = ?";
        return jdbcTemplate.query(sql, rowMapper, name);
    }

    @Override
    public Optional<Station> findById(Long id) {
        final String sql = "SELECT * FROM STATION WHERE id = ?";
        return jdbcTemplate.query(sql, rowMapper, id).stream()
                .findFirst();
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
