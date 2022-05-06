package wooteco.subway.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateStationNameException;

@Repository
public class StationJdbcDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationJdbcDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Station save(final Station station) throws IllegalArgumentException {
        if (ObjectUtils.isEmpty(station)) {
            throw new IllegalArgumentException("passed station is null");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            trySave(station, keyHolder);
        } catch (DuplicateKeyException exception) {
            throw new DuplicateStationNameException();
        }

        return new Station(keyHolder.getKey().longValue(), station.getName());
    }

    @Override
    public List<Station> findAll() {
        final String sql = "SELECT id, name FROM STATION";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        ));
    }

    @Override
    public void deleteById(final Long id) {
        final String sql = "DELETE FROM STATION WHERE id = (?)";
        jdbcTemplate.update(sql, id);
    }

    private void trySave(final Station station, final KeyHolder keyHolder) {
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO STATION (name) VALUES (?)", new String[]{"id"});
            preparedStatement.setString(1, station.getName());
            return preparedStatement;
        }, keyHolder);
    }
}
