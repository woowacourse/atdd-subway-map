package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class JdbcSectionDao {

    private static final String TABLE_NAME = "section";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleInserter;

    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleInserter = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns("id");
    }

    public Section save(Section section) {
        Map<String, ?> params = Map.of(
                "line_id", section.getLine_id(),
                "up_station_id", section.getUpStationId(),
                "down_station_id", section.getDownStationId(),
                "distance", section.getDistance());
        long savedId = simpleInserter.executeAndReturnKey(params).longValue();
        return findById(savedId);
    }

    private Section findById(long id) {
        final String sql = "SELECT * FROM section WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, getRowMapper(), id);
    }

    private RowMapper<Section> getRowMapper() {
        return (resultSet, rowNumber) -> {
            long id = resultSet.getLong("id");
            long lineId = resultSet.getLong("line_id");
            long upStationId = resultSet.getLong("up_station_id");
            long downStationId = resultSet.getLong("down_station_id");
            int distance = resultSet.getInt("distance");
            return new Section(id, lineId, upStationId, downStationId, distance);
        };
    }

    public List<Section> findByLineId(Long lineId) {
        final String sql = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), lineId);
    }

    public int deleteById(Long id) {
        final String sql = "DELETE FROM section WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public int update(Section sections) {
        final String sql = "UPDATE section SET up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";
        return jdbcTemplate.update(sql, sections.getUpStationId(), sections.getDownStationId(),
                sections.getDistance(), sections.getId());
    }
}
