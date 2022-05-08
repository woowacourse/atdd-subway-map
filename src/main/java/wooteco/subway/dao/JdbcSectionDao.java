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

import wooteco.subway.domain.Section;

@Repository
public class JdbcSectionDao implements SectionDao {

	private static final String NO_SUCH_ID_ERROR = "해당 id에 맞는 지하철 구간이 없습니다.";

	private final SimpleJdbcInsert insertActor;
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final StationDao stationDao;

	public JdbcSectionDao(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate,
		StationDao stationDao) {
		this.jdbcTemplate = jdbcTemplate;
		this.insertActor = new SimpleJdbcInsert(dataSource)
			.withTableName("section")
			.usingGeneratedKeyColumns("id");
		this.stationDao = stationDao;
	}

	@Override
	public Long save(Long lineId, Section section) {
		return insertActor.executeAndReturnKey(new MapSqlParameterSource()
				.addValue("line_id", lineId)
				.addValue("up_station_id", section.getUpStationId())
				.addValue("down_station_id", section.getDownStationId())
				.addValue("distance", section.getDistance()))
			.longValue();
	}

	@Override
	public Section findById(Long id) {
		String sql = "select * from section where id = :id";
		try {
			return jdbcTemplate.queryForObject(sql, Map.of("id", id), getSectionMapper());
		} catch (EmptyResultDataAccessException exception) {
			throw new NoSuchElementException(NO_SUCH_ID_ERROR);
		}
	}

	@Override
	public List<Section> findByLineId(Long lineId) {
		String sql = "select * from section where line_id = :lineId";
		return jdbcTemplate.query(sql, Map.of("lineId", lineId), getSectionMapper());
	}

	@Override
	public void update(Section section) {
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

	private RowMapper<Section> getSectionMapper() {
		return ((rs, rowNum) -> new Section(
			rs.getLong(1),
			stationDao.findById(rs.getLong(3)),
			stationDao.findById(rs.getLong(4)),
			rs.getInt(5)
		));
	}
}
