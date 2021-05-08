package wooteco.subway.station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.NoSuchLineException;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Station station) {
        String query = "INSERT INTO station(name) VALUES(?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public List<Station> findAll() {
        String query = "SELECT * FROM station";
        List<Station> stations = jdbcTemplate.query(query, (resultSet, rowNum) -> {
            Station station = new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
            );
            return station;
        });
        return stations;
    }

    public void delete(Long id) {
        String query = "DELETE FROM station WHERE id = ?";
        int affectedRowNumber = jdbcTemplate.update(query, id);

        if (affectedRowNumber == 0) {
            throw new NoSuchLineException("없는 노선입니다.");
        }
    }
}
