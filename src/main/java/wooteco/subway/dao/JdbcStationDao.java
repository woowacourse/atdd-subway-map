package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Station;

@Repository
public class JdbcStationDao implements StationDao {

	private static final String NO_SUCH_ID_ERROR = "해당 id에 맞는 지하철 역이 없습니다.";

	private final SimpleJdbcInsert insertActor;
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public JdbcStationDao(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.insertActor = new SimpleJdbcInsert(dataSource)
			.withTableName("station")
			.usingGeneratedKeyColumns("id");
	}

	@Override
	public Long save(Station station) {
		return insertActor.executeAndReturnKey(new BeanPropertySqlParameterSource(station))
			.longValue();
	}

	@Override
	public List<Station> findAll() {
		String sql = "select * from station";
		return jdbcTemplate.query(sql, getStationMapper());
	}

	@Override
	public Station findById(Long id) {
		String sql = "select * from station where id = :id";
		try {
			return jdbcTemplate.queryForObject(sql, Map.of("id", id), getStationMapper());
		} catch (EmptyResultDataAccessException exception) {
			throw new NoSuchElementException(NO_SUCH_ID_ERROR);
		}
	}

	private RowMapper<Station> getStationMapper() {
		return (rs, rowNum) -> new Station(
			rs.getLong(1),
			rs.getString(2)
		);
	}

	@Override
	public void remove(Long id) {
		String sql = "delete from station where id = :id";
		if (jdbcTemplate.update(sql, Map.of("id", id)) == 0) {
			throw new NoSuchElementException(NO_SUCH_ID_ERROR);
		}
	}

	@Override
	public Boolean existsByName(String name) {
		String sql = "select exists (select * from station where name = :name)";
		return jdbcTemplate.queryForObject(sql, Map.of("name", name), Boolean.class);
	}
}
