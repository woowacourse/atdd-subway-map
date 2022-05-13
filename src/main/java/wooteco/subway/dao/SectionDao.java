package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
    }

    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) ->
            Section.of(
                    resultSet.getLong("id"),
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance")
            );

    public Section insert(Long upStationId, Long downStationId, Integer distance, Long lineId) {
        Long id = simpleJdbcInsert.executeAndReturnKey(
                Map.of("line_id", lineId, "up_station_id", upStationId, "down_station_id", downStationId, "distance",
                        distance)).longValue();

        return Section.of(id, upStationId, downStationId, distance);
    }

    public void insert(Section section, Long lineId) {
        simpleJdbcInsert.execute(
                Map.of("line_id", lineId, "up_station_id", section.getUpStationId(), "down_station_id",
                        section.getDownStationId(), "distance", section.getDistance()));
    }

    public List<Section> findByLineId(Long lineId) {
        final String sql = "SELECT * FROM SECTION WHERE line_id = ?";
        return jdbcTemplate.query(sql, sectionRowMapper, lineId);
    }

    public void deleteById(Long id) {
        final String sql = "DELETE FROM SECTION WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteByLineIdAndStationId(Long lineId, Long stationId) {
        final String sql = "DELETE FROM SECTION WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        jdbcTemplate.update(sql, lineId, stationId, stationId);
    }

    public void deleteByLineId(Long lineId) {
        final String sql = "DELETE FROM SECTION WHERE line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }

    public boolean existsByStationId(Long stationId) {
        final String sql = "SELECT EXISTS (SELECT * FROM SECTION WHERE (up_station_id = ? OR down_station_id = ?))";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, stationId, stationId));
    }
}
