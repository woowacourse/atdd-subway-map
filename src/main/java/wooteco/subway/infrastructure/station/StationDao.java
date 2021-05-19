package wooteco.subway.infrastructure.station;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.value.StationId;
import wooteco.subway.domain.station.value.StationName;

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

        return new Station(
                new StationId(id),
                new StationName(station.getName())
        );
    }

    public List<Station> findAll() {
        final String sql = "SELECT * FROM STATION";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String name = rs.getString("name");

            return new Station(
                    new StationId(id),
                    new StationName(name)
            );
        });
    }

    public Station findById(Long id) {
        final String sql = "SELECT * FROM STATION WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            String name = rs.getString("name");

            return new Station(
                    new StationId(id),
                    new StationName(name)
            );
        }, id);
    }

    public int delete(Long id) {
        final String sql = "DELETE FROM STATION WHERE id = ?";

        return jdbcTemplate.update(sql, id);
    }

    //is it possible?
    public boolean isReferenced(Long id) {
        final String sql = "SELECT count(*) FROM SECTION WHERE up_station_id = ? OR down_station_id = ?";

        return jdbcTemplate.queryForObject(sql, Long.class, id, id) != 0;
    }

    public boolean contains(Long id) {
        final String sql = "SELECT count(*) FROM STATION WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, Long.class, id) != 0;
    }
}
