package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.ExceptionMessage;
import wooteco.subway.exception.InternalServerException;

@Repository
public class JdbcStationDao implements StationDao {

    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ID = "id";

    private final JdbcTemplate jdbcTemplate;

    public JdbcStationDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Station save(Station station) {
        try {
            final String sql = "INSERT INTO station SET name = ?";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement prepareStatement = con.prepareStatement(sql, new String[]{COLUMN_ID});
                prepareStatement.setString(1, station.getName());
                return prepareStatement;
            }, keyHolder);
            final long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
            return setId(station, id);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException(ExceptionMessage.DUPLICATED_STATION_NAME.getContent());
        }
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, getRowMapper());
    }

    private RowMapper<Station> getRowMapper() {
        return (resultSet, rowNumber) -> {
            String name = resultSet.getString(COLUMN_NAME);
            long id = resultSet.getLong(COLUMN_ID);
            return setId(new Station(name), id);
        };
    }

    private Station setId(Station station, long id) {
        Field field = ReflectionUtils.findField(Station.class, COLUMN_ID);
        Objects.requireNonNull(field).setAccessible(true);
        ReflectionUtils.setField(field, station, id);
        return station;
    }

    @Override
    public Integer deleteById(Long id) {
        String sql = "DELETE FROM station WHERE id = ?";
        Integer affectedRows = jdbcTemplate.update(sql, id);
        if (affectedRows == 0) {
            throw new InternalServerException(ExceptionMessage.UNKNOWN_DELETE_STATION_FAIL.getContent());
        }
        return affectedRows;
    }
}
