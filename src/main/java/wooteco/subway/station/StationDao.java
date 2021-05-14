package wooteco.subway.station;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicatedNameException;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> new Station(
        resultSet.getLong("id"),
        resultSet.getString("name")
    );

    public StationDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(final Station station) {
        try {
            final String sql = "INSERT INTO station (name) VALUES (?)";
            final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            final PreparedStatementCreator preparedStatementCreator = con -> {
                final PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, station.getName());
                return preparedStatement;
            };
            jdbcTemplate.update(preparedStatementCreator, keyHolder);
            final Long id = keyHolder.getKey().longValue();
            return findById(id).orElseThrow(() -> new DataNotFoundException("해당 Id의 지하철역이 없습니다."));
        } catch (DuplicateKeyException e) {
            throw new DuplicatedNameException("중복된 이름의 지하철역입니다.");
        }
    }

    public void deleteById(final Long id) {
        final String sql = "DELETE FROM station WHERE id = ?";
        int deletedCnt = jdbcTemplate.update(sql, id);

        if (deletedCnt < 1) {
            throw new DataNotFoundException("해당 Id의 지하철역이 없습니다.");
        }
    }

    public List<Station> findAll() {
        final String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public Optional<Station> findById(final Long id) {
        final String sql = "SELECT * FROM station WHERE id = ?";
        final List<Station> stations = jdbcTemplate.query(sql, stationRowMapper, id);
        return Optional.ofNullable(DataAccessUtils.singleResult(stations));
    }

    public Optional<Station> findByName(final String name) {
        final String sql = "SELECT * FROM station WHERE name = ?";
        final List<Station> stations = jdbcTemplate.query(sql, stationRowMapper, name);
        return Optional.ofNullable(DataAccessUtils.singleResult(stations));
    }

    public List<Station> findByIds(final List<Long> stationIds) {
        final String parameters = String.join(", ", Collections.nCopies(stationIds.size(), "?"));
        final String sql = String.format("SELECT * FROM STATION s WHERE s.id IN (%s)", parameters);
        return jdbcTemplate.query(sql, stationRowMapper, stationIds.toArray());
    }
}
