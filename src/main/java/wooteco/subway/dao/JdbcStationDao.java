package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

@Repository
public class JdbcStationDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> rowMapper = (resultSet, rowNumber) -> {
        Station station = new Station(
                resultSet.getString("name")
        );
        return setId(station, resultSet.getLong("id"));
    };

    public JdbcStationDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Station save(final Station station) {
        try {
            final String sql = "INSERT INTO station SET name = ?";

            final KeyHolder keyHolder = new GeneratedKeyHolder();
            final PreparedStatementCreator statementCreator = con -> {
                final PreparedStatement prepareStatement = con.prepareStatement(sql, new String[]{"id"});
                prepareStatement.setString(1, station.getName());
                return prepareStatement;
            };

            jdbcTemplate.update(statementCreator, keyHolder);
            final long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
            return setId(station, id);
        } catch (final DuplicateKeyException e) {
            throw new IllegalArgumentException("중복된 이름의 역은 저장할 수 없습니다.");
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
