package wooteco.subway.repository;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;
import wooteco.subway.utils.exception.IdNotFoundException;
import wooteco.subway.utils.exception.NameDuplicatedException;

@Repository
public class StationRepository {

    private static final int NO_ROW = 0;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public StationRepository(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    public Station save(final Station station) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", station.getName());
        try {
            Long id = simpleJdbcInsert.executeAndReturnKey(parameters)
                    .longValue();
            return new Station(id, station.getName());
        } catch (DuplicateKeyException e) {
            throw new NameDuplicatedException(NameDuplicatedException.NAME_DUPLICATE_MESSAGE + station.getName());
        }
    }

    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        RowMapper<Station> stationRowMapper = rowMapper();
        return namedParameterJdbcTemplate.query(sql, stationRowMapper);
    }

    public void deleteById(final Long id) {
        String sql = "DELETE FROM station WHERE id = :id";
        int rowCounts = namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource("id", id));
        if (rowCounts == NO_ROW) {
            throw new IdNotFoundException(IdNotFoundException.NO_ID_MESSAGE + id);
        }
    }

    public Boolean isNameExists(final String name) {
        String sql = "SELECT EXISTS (SELECT * FROM station WHERE name = :name)";
        SqlParameterSource parameters = new MapSqlParameterSource("name", name);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(sql, parameters, Boolean.class));
    }

    public Station findById(final Long id) {
        String sql = "SELECT * FROM station WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, parameters, rowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new IdNotFoundException(IdNotFoundException.NO_ID_MESSAGE + id);
        }
    }

    private RowMapper<Station> rowMapper() {
        return (resultSet, rowNum) -> {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            return new Station(id, name);
        };
    }
}
