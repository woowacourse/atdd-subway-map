package wooteco.subway.dao.jdbc;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@Repository
public class JdbcStationDao implements StationDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public JdbcStationDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("Station")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Long save(Station station) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(station);
        return jdbcInsert.executeAndReturnKey(parameters)
                .longValue();
    }

    @Override
    public List<Station> findAll() {
        String query = "SELECT id, name from Station";
        return jdbcTemplate.query(query,
                (resultSet, rowNum) -> new Station(
                        resultSet.getLong("id"),
                        resultSet.getString("name")
                ));
    }

    @Override
    public Station findById(Long id) {
        String query = "SELECT id, name from Station WHERE id=(:id)";
        try {
            SqlParameterSource parameters = new MapSqlParameterSource("id", id);
            return jdbcTemplate.queryForObject(query, parameters,
                    (resultSet, rowNum) -> new Station(
                            resultSet.getLong("id"),
                            resultSet.getString("name")
                    ));
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("해당 id에 맞는 지하철 역이 없습니다.");
        }
    }

    @Override
    public Boolean existsByName(String name) {
        String query = "SELECT COUNT(*) as num FROM Station WHERE name=(:name)";
        SqlParameterSource parameters = new MapSqlParameterSource("name", name);
        int count = jdbcTemplate.queryForObject(query, parameters,
                (resultSet, rowNum) -> resultSet.getInt("num"));
        return count != 0;
    }

    @Override
    public void remove(Long id) {
        String query = "DELETE FROM Station WHERE id=(:id)";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(query, parameters);
    }
}
