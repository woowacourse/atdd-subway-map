package wooteco.subway.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
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
    public Long save(final Station station) {
        final String sql = "INSERT INTO STATION (name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
                preparedStatement.setString(1, station.getName());
                return preparedStatement;
            }, keyHolder);
        } catch (DuplicateKeyException exception) {
            throw new DuplicateStationNameException();
        }

        return keyHolder.getKey().longValue();
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
    public Station findById(Long id) {
        final String sql = "SELECT id, name FROM STATION WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        ), id);
    }

    @Override
    public void deleteById(final Long id) {
        final String sql = "DELETE FROM STATION WHERE id = (?)";
        jdbcTemplate.update(sql, id);
    }
}
