package wooteco.subway.dao;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class StationDao {
    private static final RowMapper<Station> ROW_MAPPER = (resultSet, rowNumber) -> {
        long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        return new Station(id, name);
    };
    private static final int ROW_COUNTS_FOR_ID_NOT_FOUND = 0;

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Station station) {
        String query = "INSERT INTO Station (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = getPreparedStatementCreator(station, query);
        try {
            jdbcTemplate.update(preparedStatementCreator, keyHolder);
            return keyHolder.getKey().longValue();
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new SubwayException(ExceptionStatus.DUPLICATED_NAME);
        }
    }

    private PreparedStatementCreator getPreparedStatementCreator(Station station, String query) {
        return (connection) -> {
            PreparedStatement prepareStatement = connection.prepareStatement(query, new String[]{"id"});
            prepareStatement.setString(1, station.getName());
            return prepareStatement;
        };
    }

    public List<Station> findAll() {
        String query = "SELECT ID, NAME FROM STATION";
        return jdbcTemplate.query(query, ROW_MAPPER);
    }

    public Station findById(long id) {
        String query = "SELECT ID, NAME FROM STATION WHERE ID = ?";
        try {
            return jdbcTemplate.queryForObject(query, ROW_MAPPER, id);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            throw new SubwayException(ExceptionStatus.ID_NOT_FOUND);
        }
    }

    public void deleteById(long id) {
        String query = "DELETE FROM STATION WHERE ID = ?";
        int affectedRowCounts = jdbcTemplate.update(query, id);
        if (affectedRowCounts == ROW_COUNTS_FOR_ID_NOT_FOUND) {
            throw new SubwayException(ExceptionStatus.ID_NOT_FOUND);
        }
    }
}
