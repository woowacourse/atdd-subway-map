package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Station;

@Repository
public class JdbcStationDao implements StationDao {

    public static final RowMapper<Station> STATION_ROW_MAPPER = (resultSet, rowNum) -> new Station(
        resultSet.getLong("id"),
        resultSet.getString("name")
    );
    private final SimpleJdbcInsert jdbcInsert;
    private final JdbcTemplate jdbcTemplate;

    public JdbcStationDao(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("station")
            .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Station save(Station station) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(station);
        final Long id = jdbcInsert.executeAndReturnKey(param).longValue();
        return new Station(id, station.getName());
    }

    @Override
    public List<Station> findAll() {
        final String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, STATION_ROW_MAPPER);
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM station WHERE id = ?";
        final int deletedCount = jdbcTemplate.update(sql, id);
        return isUpdated(deletedCount);
    }

    @Override
    public Optional<Station> findByName(String name) {
        final String sql = "SELECT * FROM station WHERE name = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, STATION_ROW_MAPPER, name));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private boolean isUpdated(int updatedCount) {
        return updatedCount == 1;
    }
}
