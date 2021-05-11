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
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("ID");
    }

    public long save(Section section) {
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(section);
        return simpleJdbcInsert.executeAndReturnKey(sqlParameterSource)
                .longValue();
    }

    public List<Section> findAllByLineId(long lineId) {
        String query = "SELECT ID, DISTANCE, LINE_ID FROM SECTION WHERE LINE_ID = :LINE_ID";
        RowMapper<Section> sectionRowMapper = (resultSet, rowNumber) -> {
            long id = resultSet.getLong("ID");
            int distance = resultSet.getInt("DISTANCE");
            return Section.builder().id(id).distance(distance).lineId(lineId).build();
        };
        return jdbcTemplate.query(query, Collections.singletonMap("LINE_ID", lineId), sectionRowMapper);
    }

    public List<Section> finAllByStationId(long stationId) {
        String query = "SELECT ID, DISTANCE, LINE_ID FROM SECTION WHERE :STATION_ID IN (UP_STATION_ID, DOWN_STATION_ID)";
        RowMapper<Section> sectionRowMapper = (resultSet, rowNumber) -> {
            long id = resultSet.getLong("ID");
            int distance = resultSet.getInt("DISTANCE");
            long lineId = resultSet.getLong("LINE_ID");
            return Section.builder().id(id).distance(distance).lineId(lineId).build();
        };
        return jdbcTemplate.query(query, Collections.singletonMap("STATION_ID", stationId), sectionRowMapper);
    }

    public List<Long> findStationIdsById(long id) {
        String query = "SELECT UP_STATION_ID, DOWN_STATION_ID FROM SECTION WHERE ID = :ID";
        RowMapper<List<Long>> stationIdsRowMapper = (resultSet, rowNumber) -> {
            long upStationId = resultSet.getLong("UP_STATION_ID");
            long downStationId = resultSet.getLong("DOWN_STATION_ID");
            return Arrays.asList(upStationId, downStationId);
        };
        return jdbcTemplate.queryForObject(query, Collections.singletonMap("ID", id), stationIdsRowMapper);
    }

    public void update(Section section) {
        String query = "UPDATE SECTION SET UP_STATION_ID = :UP_STATION_ID, DOWN_STATION_ID = :DOWN_STATION_ID, DISTANCE = :DISTANCE WHERE ID = :ID";
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
        parameters.put("UP_STATION_ID", upStationId);
        parameters.put("DOWN_STATION_ID", downStationId);
        parameters.put("DISTANCE", distance);
        parameters.put("ID", id);
        return parameters;
    }

    private void validateId(int affectedRowCounts) {
        if (affectedRowCounts == ROW_COUNTS_FOR_ID_NOT_FOUND) {
            throw new SubwayException(ExceptionStatus.ID_NOT_FOUND);
        }
    }

    public void deleteById(long id) {
        String query = "DELETE FROM SECTION WHERE ID = :ID";
        int affectedRowCounts = jdbcTemplate.update(query, Collections.singletonMap("ID", id));
        validateId(affectedRowCounts);
    }
}
