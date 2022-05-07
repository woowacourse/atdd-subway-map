package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Line;

@Repository
public class JdbcLineDao implements LineDao {

	private static final String NO_SUCH_ID_ERROR = "해당 id에 맞는 지하철 노선이 없습니다.";
	private final SimpleJdbcInsert insertActor;
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final SectionDao sectionDao;

	public JdbcLineDao(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate,
		SectionDao sectionDao) {
		this.jdbcTemplate = jdbcTemplate;
		this.insertActor = new SimpleJdbcInsert(dataSource)
			.withTableName("line")
			.usingGeneratedKeyColumns("id");
		this.sectionDao = sectionDao;
	}

	@Override
	public Long save(Line line) {
		long lineId = insertActor.executeAndReturnKey(new BeanPropertySqlParameterSource(line))
			.longValue();
		line.getSections().forEach(section -> sectionDao.save(lineId, section));
		return lineId;
	}

	@Override
	public List<Line> findAll() {
		String sql = "select * from line";
		return jdbcTemplate.query(sql, getLineMapper()).stream()
			.map(line -> line.createWithSection(sectionDao.findByLineId(line.getId())))
			.collect(Collectors.toList());
	}

	@Override
	public Line findById(Long id) {
		String sql = "select * from line where id = :id";
		try {
			Line line = jdbcTemplate.queryForObject(sql, Map.of("id", id), getLineMapper());
			return Objects.requireNonNull(line)
				.createWithSection(sectionDao.findByLineId(line.getId()));
		} catch (EmptyResultDataAccessException exception) {
			throw new NoSuchElementException(NO_SUCH_ID_ERROR);
		}
	}

	private RowMapper<Line> getLineMapper() {
		return ((rs, rowNum) -> new Line(
			rs.getLong(1),
			rs.getString(2),
			rs.getString(3))
		);
	}

	@Override
	public void update(Line line) {
		String sql = "update line set "
			+ "name = :name, "
			+ "color = :color "
			+ "where id = :id";
		if (jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(line)) == 0) {
			throw new NoSuchElementException(NO_SUCH_ID_ERROR);
		}
	}

	@Override
	public void remove(Long id) {
		String sql = "delete from line where id = :id";
		if (jdbcTemplate.update(sql, Map.of("id", id)) == 0) {
			throw new NoSuchElementException(NO_SUCH_ID_ERROR);
		}
	}

	@Override
	public Boolean existsByName(String name) {
		String sql = "select exists (select * from line where name = :name)";
		return jdbcTemplate.queryForObject(sql, Map.of("name", name), Boolean.class);
	}
}
