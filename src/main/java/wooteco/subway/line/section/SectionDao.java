package wooteco.subway.line.section;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.DataNotFoundException;

@Repository
public class SectionDao {

    private static final int SUCCESSFUL_AFFECTED_COUNT = 1;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) -> Section.Builder()
        .id(resultSet.getLong("id"))
        .lineId(resultSet.getLong("line_id"))
        .upStationId(resultSet.getLong("up_station_id"))
        .downStationId(resultSet.getLong("down_station_id"))
        .distance(resultSet.getInt("distance"))
        .build();

    public SectionDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Section save(final Section section) {
        final String sql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) "
            + "VALUES (:lineId, :upStationId, :downStationId, :distance)";
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        final SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(section);
        namedParameterJdbcTemplate.update(sql, sqlParameterSource, keyHolder);
        final long id = keyHolder.getKey().longValue();
        return findById(id).get();
    }

    public Optional<Section> findById(final Long id) {
        final String sql = "SELECT * FROM section WHERE id = :id";
        final List<Section> sections = namedParameterJdbcTemplate.query(
            sql, Collections.singletonMap("id", id), sectionRowMapper
        );
        return Optional.ofNullable(DataAccessUtils.singleResult(sections));
    }

    public List<Section> findByLineId(final Long lineId) {
        final String sql = "SELECT * FROM section WHERE line_id = :lineId";
        return namedParameterJdbcTemplate.query(
            sql, Collections.singletonMap("lineId", lineId), sectionRowMapper
        );
    }

    public void update(final Section updatedSection) {
        final String sql = "UPDATE section "
            + "SET up_station_id = :upStationId, down_station_id = :downStationId, distance = :distance WHERE id = :id";
        final SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(updatedSection);
        final int updatedCount = namedParameterJdbcTemplate.update(sql, sqlParameterSource);

        if (updatedCount < SUCCESSFUL_AFFECTED_COUNT) {
            throw new DataNotFoundException("해당 Id의 구간이 없습니다.");
        }
    }

    public void deleteById(final long id) {
        final String sql = "DELETE FROM section WHERE id = :id";
        int deletedCount = namedParameterJdbcTemplate.update(sql, Collections.singletonMap("id", id));
        if (deletedCount < SUCCESSFUL_AFFECTED_COUNT) {
            throw new DataNotFoundException("해당 Id의 구간이 없습니다.");
        }
    }
}
