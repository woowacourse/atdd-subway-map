package wooteco.subway.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.ExceptionInformation;
import wooteco.subway.exception.SubwayException;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

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
        executeSaveQuery(station, query, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private void executeSaveQuery(Station station, String query, KeyHolder keyHolder) {
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
                ps.setString(1, station.getName());
                return ps;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new SubwayException(ExceptionInformation.DUPLICATE_STATION_NAME_WHEN_INSERT);
        }
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
            throw new SubwayException(ExceptionInformation.STATION_NOT_FOUND_WHEN_DELETE);
        }
    }
}
