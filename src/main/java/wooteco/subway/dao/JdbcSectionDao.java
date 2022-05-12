package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class JdbcSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public JdbcSectionDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Long save(Section section) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("line_id", section.getLineId());
        parameters.put("up_station_id", section.getUpStationId());
        parameters.put("down_station_id", section.getDownStationId());
        parameters.put("distance", section.getDistance());
        parameters.put("line_order", section.getLineOrder());

        return insertActor.executeAndReturnKey(parameters).longValue();
    }

    @Override
    public void updateLineOrder(Long lineId, Long lineOrder) {
        String sql = "UPDATE \"SECTION\" SET line_order = line_order + 1 WHERE line_id = (?) AND line_order >= (?)";
        jdbcTemplate.update(sql, lineId, lineOrder);
    }

    @Override
    public boolean existByLineId(Long lineId) {
        String sql = "SELECT EXISTS ("
                + "SELECT * FROM \"SECTION\" WHERE line_id = (?)"
                + ")";
        return jdbcTemplate.queryForObject(sql, boolean.class, lineId);
    }

    @Override
    public List<Section> findAllByLineId(Long lineId) {
        String sql = "SELECT * from \"SECTION\" WHERE line_id = (?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Section(
                    rs.getLong("id"),
                    rs.getLong("line_id"),
                    rs.getLong("up_station_id"),
                    rs.getLong("down_station_id"),
                    rs.getInt("distance"),
                    rs.getLong("line_order")
            );
        }, lineId);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM \"SECTION\" WHERE id = (?)";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Section> findByLineIdAndStationId(long lineId, long stationId) {
        String sql = "SELECT * FROM \"SECTION\""
                + " WHERE line_id = (?) AND (up_station_id = (?) OR down_station_id = (?))";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Section(
                    rs.getLong("id"),
                    rs.getLong("line_id"),
                    rs.getLong("up_station_id"),
                    rs.getLong("down_station_id"),
                    rs.getInt("distance"),
                    rs.getLong("line_order")
            );
        }, lineId, stationId, stationId);
    }

    @Override
    public void updateLineOrderByDec(long lineId, long lineOrder) {
        String sql = "UPDATE \"SECTION\" SET line_order = line_order - 1 WHERE line_id = (?) AND line_order > (?)";
        jdbcTemplate.update(sql, lineId, lineOrder);
    }
}
