package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.station.NoSuchStationException;

@Repository
public class JdbcStationDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final RowMapper<Station> rowMapper = (resultSet, rowNumber) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    public JdbcStationDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<Station> insert(final Station station) {
        try {
            final SqlParameterSource parameters = new BeanPropertySqlParameterSource(station);
            final long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
            return Optional.of(new Station(id, station.getName()));
        } catch (final DuplicateKeyException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Station> findById(final Long id) {
        try {
            final String sql = "SELECT * FROM station WHERE id = ?";
            final Station station = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(station);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Station> findAll() {
        final String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public List<Station> findAllByLineId(final Long lineId) {
        final String sql = "SELECT * "
                + "FROM station "
                + "WHERE id IN"
                + "(SELECT st.id FROM section AS se INNER JOIN station AS st ON se.up_station_id = st.id WHERE se.line_id = ?) "
                + "OR id IN"
                + "(SELECT st.id FROM section AS se INNER JOIN station AS st ON se.down_station_id = st.id WHERE se.line_id = ?)";
        return jdbcTemplate.query(sql, rowMapper, lineId, lineId);
    }

    @Override
    public Integer deleteById(final Long id) {
        final String sql = "DELETE FROM station WHERE id = ?";
        final int affectedRows = jdbcTemplate.update(sql, id);
        if (affectedRows == 0) {
            throw new NoSuchStationException();
        }
        return affectedRows;
    }
}
