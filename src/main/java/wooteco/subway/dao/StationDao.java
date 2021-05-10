package wooteco.subway.dao;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;

import java.util.List;
import java.util.Optional;

@Repository
public class StationDao {
    private static final RowMapper<Station> BASIC_STATION_ROW_MAPPER = (resultSet, rowNumber) -> {
        long id = resultSet.getLong("ID");
        String name = resultSet.getString("NAME");
        return new Station(id, name);
    };
    private static final int ROW_COUNTS_FOR_ID_NOT_FOUND = 0;

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("STATION")
                .usingGeneratedKeyColumns("ID");
    }

    public long save(Station station) {
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(station);
        try {
            return simpleJdbcInsert.executeAndReturnKey(sqlParameterSource)
                    .longValue();
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new SubwayException(ExceptionStatus.DUPLICATED_NAME);
        }
    }

    public List<Station> findAll() {
        String query = "SELECT ID, NAME FROM STATION";
        return jdbcTemplate.query(query, BASIC_STATION_ROW_MAPPER);
    }

    public Optional<Station> findById(long id) {
        String query = "SELECT ID, NAME FROM STATION WHERE ID = ?";
        try {
            Station station = jdbcTemplate.queryForObject(query, BASIC_STATION_ROW_MAPPER, id);
            return Optional.ofNullable(station);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return Optional.empty();
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
