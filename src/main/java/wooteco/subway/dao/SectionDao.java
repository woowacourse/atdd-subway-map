package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.entity.SectionEntity;
import wooteco.subway.exception.notfound.NotFoundException;
import wooteco.subway.exception.notfound.NotFoundSectionException;

@Repository
public class SectionDao {

    private static final RowMapper<SectionEntity> ROW_MAPPER = (resultSet, rowNum) -> new SectionEntity(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance"));

    private final SimpleJdbcInsert insertActor;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SectionDao(final DataSource dataSource) {
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Long save(final SectionEntity section) {
        final SqlParameterSource parameters = new BeanPropertySqlParameterSource(section);
        return insertActor.executeAndReturnKey(parameters).longValue();
    }

    public void batchSave(final List<SectionEntity> sections) {
        final String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) "
                + "VALUES (:lineId, :upStationId, :downStationId, :distance)";
        jdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(sections));
    }

    public List<SectionEntity> findAllByLineId(final Long id) {
        try {
            final String sql = "SELECT * FROM SECTION WHERE line_id = :id";
            return jdbcTemplate.query(sql, Map.of("id", id), ROW_MAPPER);
        } catch (final EmptyResultDataAccessException e) {
            throw new NotFoundException("존재하지 않는 노선(ID: " + id + ")입니다.");
        }
    }

    public SectionEntity findById(final Long id) {
        try {
            final String sql = "SELECT * FROM SECTION WHERE id = :id";
            return jdbcTemplate.queryForObject(sql, Map.of("id", id), ROW_MAPPER);
        } catch (final EmptyResultDataAccessException e) {
            throw new NotFoundSectionException();
        }
    }

    public void deleteById(final Long id) {
        final String sql = "DELETE FROM SECTION WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }

    public void batchDelete(final List<SectionEntity> sections) {
        final String sql = "DELETE FROM SECTION WHERE id = :id";
        jdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(sections));
    }
}
