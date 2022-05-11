package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import wooteco.subway.dao.table.SectionTable;

@Repository
public class SectionDao {

	private static final String NO_SUCH_ID_ERROR = "해당 id에 맞는 지하철 구간이 없습니다.";

	private final SimpleJdbcInsert insertActor;
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public SectionDao(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.insertActor = new SimpleJdbcInsert(dataSource)
			.withTableName("section")
			.usingGeneratedKeyColumns("id");
	}

	public Long save(SectionTable section) {
		return insertActor.executeAndReturnKey(new MapSqlParameterSource()
			.addValue("line_id", section.getLineId())
			.addValue("up_station_id", section.getUpStationId())
			.addValue("down_station_id", section.getDownStationId())
			.addValue("distance", section.getDistance())
		).longValue();
	}

	public SectionTable findById(Long id) {
		String sql = "select * from section where id = :id";
		try {
			return jdbcTemplate.queryForObject(sql, Map.of("id", id), getSectionMapper());
		} catch (EmptyResultDataAccessException exception) {
			throw new NoSuchElementException(NO_SUCH_ID_ERROR);
		}
	}

	public List<SectionTable> findByLineId(Long lineId) {
		String sql = "select * from section where line_id = :lineId";
		return jdbcTemplate.query(sql, Map.of("lineId", lineId), getSectionMapper());
	}

	private RowMapper<SectionTable> getSectionMapper() {
		return ((rs, rowNum) -> new SectionTable(
			rs.getLong(1),
			rs.getLong(2),
			rs.getLong(3),
			rs.getLong(4),
			rs.getInt(5)
		));
	}

	public void update(SectionTable section) {
		String sql = "update section set "
			+ "up_station_id = :upStationId, "
			+ "down_station_id = :downStationId, "
			+ "distance = :distance "
			+ "where id = :id";
		jdbcTemplate.update(sql, new MapSqlParameterSource()
			.addValue("upStationId", section.getUpStationId())
			.addValue("downStationId", section.getDownStationId())
			.addValue("distance", section.getDistance())
			.addValue("id", section.getId())
		);
	}

	public void remove(Long id) {
		String sql = "delete from section where id = :id";
		if (jdbcTemplate.update(sql, Map.of("id", id)) == 0) {
			throw new NoSuchElementException(NO_SUCH_ID_ERROR);
		}
	}

	public Boolean existByStationId(Long stationId) {
		String sql = "select exists (select * from section "
			+ "where up_station_id = :stationId or down_station_id = :stationId)";
		return jdbcTemplate.queryForObject(sql, Map.of("stationId", stationId), Boolean.class);
	}
}
