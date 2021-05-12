package wooteco.subway.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.section.Section;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class SectionDao {
    private static final int ROW_COUNTS_FOR_ID_NOT_FOUND = 0;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public SectionDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public long save(Section section) {
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(section);
        return simpleJdbcInsert.executeAndReturnKey(sqlParameterSource)
                .longValue();
    }

    public List<Section> findAllByLineId(long lineId) {
        String query = "select id, distance, line_id from section where line_id = :line_id";
        RowMapper<Section> sectionRowMapper = (resultSet, rowNumber) -> {
            long id = resultSet.getLong("id");
            int distance = resultSet.getInt("distance");
            return Section.builder().id(id).distance(distance).lineId(lineId).build();
        };
        return jdbcTemplate.query(query, Collections.singletonMap("line_id", lineId), sectionRowMapper);
    }

    public List<Section> findAllByStationId(long stationId) {
        String query = "select id, distance, line_id from section where :station_id in (up_station_id, down_station_id)";
        RowMapper<Section> sectionRowMapper = (resultSet, rowNumber) -> {
            long id = resultSet.getLong("id");
            int distance = resultSet.getInt("distance");
            long lineId = resultSet.getLong("line_id");
            return Section.builder().id(id).distance(distance).lineId(lineId).build();
        };
        return jdbcTemplate.query(query, Collections.singletonMap("station_id", stationId), sectionRowMapper);
    }

    public List<Long> findStationIdsById(long id) {
        String query = "select up_station_id, down_station_id from section where id = :id";
        RowMapper<List<Long>> stationIdsRowMapper = (resultSet, rowNumber) -> {
            long upStationId = resultSet.getLong("up_station_id");
            long downStationId = resultSet.getLong("down_station_id");
            return Arrays.asList(upStationId, downStationId);
        };
        return jdbcTemplate.queryForObject(query, Collections.singletonMap("id", id), stationIdsRowMapper);
    }

    public void update(Section section) {
        String query = "update section set up_station_id = :up_station_id, down_station_id = :down_station_id, distance = :distance where id = :id";
        Map<String, Object> parameters = generateParameters(section);
        int affectedRowCounts = jdbcTemplate.update(query, parameters);
        validateId(affectedRowCounts);
    }

    private Map<String, Object> generateParameters(Section section) {
        long upStationId = section.getUpStationId();
        long downStationId = section.getDownStationId();
        long distance = section.getDistance();
        long id = section.getId();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("up_station_id", upStationId);
        parameters.put("down_station_id", downStationId);
        parameters.put("distance", distance);
        parameters.put("id", id);
        return parameters;
    }

    private void validateId(int affectedRowCounts) {
        if (affectedRowCounts == ROW_COUNTS_FOR_ID_NOT_FOUND) {
            throw new SubwayException(ExceptionStatus.ID_NOT_FOUND);
        }
    }

    public void deleteById(long id) {
        String query = "delete from section where id = :id";
        int affectedRowCounts = jdbcTemplate.update(query, Collections.singletonMap("id", id));
        validateId(affectedRowCounts);
    }

    public void deleteAllById(long lineId) {
        String query = "delete from section where line_id = :line_id";
        jdbcTemplate.update(query, Collections.singletonMap("line_id", lineId));
    }
}
