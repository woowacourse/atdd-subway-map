package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.section.Section;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;

import java.util.Arrays;
import java.util.List;

@Repository
public class SectionDao {
    private static final int ROW_COUNTS_FOR_ID_NOT_FOUND = 0;

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("ID");
    }

    public long save(Section section) {
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(section);
        return simpleJdbcInsert.executeAndReturnKey(sqlParameterSource)
                .longValue();
    }

    public List<Section> findAllByLineId(long lineId) {
        String query = "SELECT ID, DISTANCE, LINE_ID FROM SECTION WHERE LINE_ID = ?";
        RowMapper<Section> sectionRowMapper = (resultSet, rowNumber) -> {
            long id = resultSet.getLong("ID");
            int distance = resultSet.getInt("DISTANCE");
            return Section.builder()
                    .id(id)
                    .distance(distance)
                    .lineId(lineId)
                    .build();
        };
        return jdbcTemplate.query(query, sectionRowMapper, lineId);
    }

    public List<Section> finAllByStationId(long stationId) {
        String query = "SELECT ID, DISTANCE, LINE_ID FROM SECTION WHERE UP_STATION_ID = ? OR DOWN_STATION_ID = ?";
        RowMapper<Section> sectionRowMapper = (resultSet, rowNumber) -> {
            long id = resultSet.getLong("ID");
            int distance = resultSet.getInt("DISTANCE");
            long lineId = resultSet.getLong("LINE_ID");
            return Section.builder()
                    .id(id)
                    .distance(distance)
                    .lineId(lineId)
                    .build();
        };
        return jdbcTemplate.query(query, sectionRowMapper, stationId, stationId);
    }

    public List<Long> findStationIdsById(long id) {
        String query = "SELECT UP_STATION_ID, DOWN_STATION_ID FROM SECTION WHERE ID = ?";
        RowMapper<List<Long>> stationIdsRowMapper = (resultSet, rowNumber) -> {
            long upStationId = resultSet.getLong("UP_STATION_ID");
            long downStationId = resultSet.getLong("DOWN_STATION_ID");
            return Arrays.asList(upStationId, downStationId);
        };
        return jdbcTemplate.queryForObject(query, stationIdsRowMapper, id);
    }

    public void update(Section section) {
        String query = "UPDATE SECTION SET UP_STATION_ID = ?, DOWN_STATION_ID = ?, DISTANCE = ? WHERE ID = ?";
        long upStationId = section.getUpStationId();
        long downStationId = section.getDownStationId();
        long distance = section.getDistance();
        long id = section.getId();
        int affectedRowCounts = jdbcTemplate.update(query, upStationId, downStationId, distance, id);
        validateId(affectedRowCounts);
    }

    private void validateId(int affectedRowCounts) {
        if (affectedRowCounts == ROW_COUNTS_FOR_ID_NOT_FOUND) {
            throw new SubwayException(ExceptionStatus.ID_NOT_FOUND);
        }
    }

    public void deleteById(long id) {
        String query = "DELETE FROM SECTION WHERE ID = ?";
        int affectedRowCounts = jdbcTemplate.update(query, id);
        validateId(affectedRowCounts);
    }
}
