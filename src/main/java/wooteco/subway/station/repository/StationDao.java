package wooteco.subway.station.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        final String sql = "INSERT INTO STATION (name) VALUES (?)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            final PreparedStatement pstmt = connection.prepareStatement(sql, new String[]{"id"});
            pstmt.setString(1, station.getName());
            return pstmt;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        return new Station(id, station.getName());
    }

    public List<Station> findAll() {
        final String sql = "SELECT * FROM STATION";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String name = rs.getString("name");

            return new Station(id, name);
        });
    }

    public void deleteById(Long id) {
        final String sql = "DELETE FROM STATION WHERE id = ?";

        jdbcTemplate.update(sql, id);
    }

    public Station findById(final Long id) {
        final String sql = "SELECT * FROM STATION WHERE id = ?";

        return jdbcTemplate.query(sql, rs -> {
            String name = rs.getString("name");
            return new Station(id, name);
        });
    }
}
