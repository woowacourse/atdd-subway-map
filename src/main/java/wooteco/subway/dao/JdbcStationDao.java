package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

@Repository
public class JdbcStationDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final RowMapper<Station> rowMapper = (resultSet, rowNumber) -> {
        Station station = new Station(
                resultSet.getString("name")
        );
        return setId(station, resultSet.getLong("id"));
    };

    public JdbcStationDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("STATION")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<Station> save(final Station station) {
        try {
            final SqlParameterSource parameters = new BeanPropertySqlParameterSource(station);
            final long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
            return Optional.of(setId(station, id));
        } catch (final DuplicateKeyException e) {
            return Optional.empty();
        }
    }

    private Station setId(final Station station, final long id) {
        final Field field = ReflectionUtils.findField(Station.class, "id");
        Objects.requireNonNull(field).setAccessible(true);
        ReflectionUtils.setField(field, station, id);
        return station;
    }

    @Override
    public List<Station> findAll() {
        final String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Integer deleteById(final Long id) {
        final String sql = "DELETE FROM station WHERE id = ?";
        final int affectedRows = jdbcTemplate.update(sql, id);
        if (affectedRows == 0) {
            throw new IllegalArgumentException("id가 일치하는 역이 존재하지 않습니다.");
        }
        return affectedRows;
    }
}
