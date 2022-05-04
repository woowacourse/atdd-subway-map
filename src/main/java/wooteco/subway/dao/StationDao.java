package wooteco.subway.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.NotFoundException;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public StationDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("STATION")
                .usingGeneratedKeyColumns("id");
    }

    public Station save(Station station) {
        final SqlParameterSource parameters = new BeanPropertySqlParameterSource(station);
        final Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Station(id, station.getName());
    }

    private RowMapper<Station> rowMapper() {
        return (rs, rowNum) -> {
            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            return new Station(id, name);
        };
    }

    public List<Station> findAll() {
        String SQL = "select * from station;";
        return jdbcTemplate.query(SQL, rowMapper());
    }

    public void deleteById(Long id) {
        findById(id);
        String SQL = "delete from station where id = ?";
        jdbcTemplate.update(SQL, id);
    }

    private Station findById(Long id) {
        String SQL = "select * from station where id = ?;";
        try {
            return jdbcTemplate.queryForObject(SQL, rowMapper(), id);
        } catch (DataAccessException e) {
            throw new NotFoundException("id에 맞는 지하철역이 없습니다.");
        }
    }
}
