package wooteco.subway.dao;

import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.exception.notfound.NotFoundException;
import wooteco.subway.exception.notfound.NotFoundSectionException;

@Repository
public class SectionDao {

    private static final RowMapper<Section> ROW_MAPPER = (resultSet, rowNum) -> new Section(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance"));

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public SectionDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
    }

    public Long save(final Section section) {
        final SqlParameterSource parameters = new BeanPropertySqlParameterSource(section);
        return insertActor.executeAndReturnKey(parameters).longValue();
    }

    public Sections findAllByLineId(final Long id) {
        try {
            final String sql = "SELECT * FROM SECTION WHERE line_id = ?";
            return new Sections(jdbcTemplate.query(sql, ROW_MAPPER, id));
        } catch (final EmptyResultDataAccessException e) {
            throw new NotFoundException("존재하지 않는 노선(ID: " + id + ")입니다.");
        }
    }

    public Section findById(final Long id) {
        try {
            final String sql = "SELECT * FROM SECTION WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, ROW_MAPPER, id);
        } catch (final EmptyResultDataAccessException e) {
            throw new NotFoundSectionException();
        }
    }

    public void deleteById(final Long id) {
        final String sql = "DELETE FROM SECTION WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
