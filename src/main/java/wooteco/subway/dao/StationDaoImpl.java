package wooteco.subway.dao;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.StationDuplicateException;

@Repository
public class StationDaoImpl implements StationDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) ->
        new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
        );

    public StationDaoImpl(final DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("station")
            .usingGeneratedKeyColumns("id");
    }

    @Override
    public Station save(final Station station) {
        checkDuplicateName(station);
        final BeanPropertySqlParameterSource parameters = new BeanPropertySqlParameterSource(station);
        final Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Station(id, station.getName());
    }

    private void checkDuplicateName(final Station station) {
        if (isExistSameName(station)) {
            throw new StationDuplicateException("이미 존재하는 지하철역 이름입니다.");
        }
    }

    private boolean isExistSameName(final Station station) {
        String sql = "SELECT count(*) FROM station WHERE name = :name";
        final BeanPropertySqlParameterSource parameters = new BeanPropertySqlParameterSource(station);
        return namedParameterJdbcTemplate.queryForObject(sql, parameters, Integer.class) > 0;
    }

    @Override
    public void deleteAll() {
        String sql = "TRUNCATE TABLE station";
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource());
        String resetIdSql = "ALTER TABLE station ALTER COLUMN id RESTART WITH 1";
        namedParameterJdbcTemplate.update(resetIdSql, new MapSqlParameterSource());
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        return namedParameterJdbcTemplate.query(sql, stationRowMapper);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM station WHERE id = :id";
        MapSqlParameterSource parameters = new MapSqlParameterSource("id", id);
        namedParameterJdbcTemplate.update(sql, parameters);
    }
}
