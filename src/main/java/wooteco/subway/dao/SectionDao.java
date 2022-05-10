package wooteco.subway.dao;

import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDao {

    private static final String TABLE_NAME = "SECTION";
    private static final String KEY_NAME = "id";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.insertActor = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(KEY_NAME);
    }

    public Section save(Section section) {
        Map<String, Object> params = Map.of(
                "line_id", section.getLineId(),
                "up_station_id", section.getUpStationId(),
                "down_station_id", section.getDownStationId(),
                "distance", section.getDistance());

        Long id = insertActor.executeAndReturnKey(params).longValue();

        return findById(id).get();
    }

    public Optional<Section> findById(Long id) {
        String sql = "select id, line_id, up_station_id, down_station_id, distance from SECTION where id = :id";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, Map.of("id", id), generateMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private RowMapper<Section> generateMapper() {
        return (resultSet, rowNum) ->
                new Section(
                        resultSet.getLong("id"),
                        resultSet.getLong("line_id"),
                        resultSet.getLong("up_station_id"),
                        resultSet.getLong("down_station_id"),
                        resultSet.getInt("distance")
                );
    }
}
