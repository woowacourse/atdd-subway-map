package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
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

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public JdbcSectionDao(final DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("section")
            .usingGeneratedKeyColumns("id");
    }

    @Override
    public Section save(final Section section) {
        final MapSqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("line_id", section.getLineId())
            .addValue("up_station_id", section.getUpStationId())
            .addValue("down_station_id", section.getDownStationId())
            .addValue("distance", section.getLineId());
        final Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Section(id, section.getLineId(), section.getUpStationId(), section.getDownStationId(),
            section.getDistance());
    }

    @Override
    public void deleteById(final Long id) {
        final String sql = "DELETE FROM section WHERE id = :id";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("id", id);
        namedParameterJdbcTemplate.update(sql, parameters);
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    public Optional<Section> findById(final Long id) {
        final String sql = "SELECT * FROM section WHERE id = :id";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("id", id);
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, parameters, getSectionRowMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Section> findSectionStationsByLineId(final Long lineId) {
        final String sql = "SELECT * FROM section WHERE line_id = :lineId";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("lineId", lineId);
        return namedParameterJdbcTemplate.query(sql, parameters, getSectionRowMapper());
    }

    @Override
    public void deleteAllByLineId(final Long lineId) {
        final String sql = "DELETE FROM section WHERE line_id = :lineId";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("lineId", lineId);
        namedParameterJdbcTemplate.update(sql, parameters);
    }

    private RowMapper<Section> getSectionRowMapper() {
        return (resultSet, rowNum) -> (
            new Section(
                resultSet.getLong("id"),
                resultSet.getLong("line_id"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id"),
                resultSet.getInt("distance")
            )
        );
    }
}
