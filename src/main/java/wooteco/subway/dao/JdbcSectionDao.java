package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.support.DataAccessUtils;
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
        parameters.put("line_id", section.getLine().getId());
        parameters.put("up_station_id", section.getUpStation().getId());
        parameters.put("down_station_id", section.getDownStation().getId());
        parameters.put("distance", section.getDistance());
        parameters.put("line_order", section.getLineOrder());

        return insertActor.executeAndReturnKey(parameters).longValue();
    }

    @Override
    public boolean existByLineIdAndStationId(Long lineId, Long stationId) {
        String sql = "SELECT EXISTS ("
                + "SELECT * FROM \"SECTION\" WHERE line_id = (?) AND (up_station_id = (?) OR down_station_id = (?))"
                + ")";

        return jdbcTemplate.queryForObject(sql, boolean.class, lineId, stationId, stationId);
    }

    @Override
    public Optional<Long> findIdByLineIdAndUpStationId(Long lineId, Long stationId) {
        String sql = "SELECT id FROM \"SECTION\" WHERE line_id = (?) AND up_station_id = (?)";
        List<Long> id = jdbcTemplate.query(sql,
                (resultSet, rowMapper) -> resultSet.getLong("id"),
                lineId, stationId);

        return Optional.ofNullable(DataAccessUtils.singleResult(id));
    }

    @Override
    public Optional<Long> findIdByLineIdAndDownStationId(Long lineId, Long stationId) {
        String sql = "SELECT id FROM \"SECTION\" WHERE line_id = (?) AND down_station_id = (?)";
        List<Long> id = jdbcTemplate.query(sql,
                (resultSet, rowMapper) -> resultSet.getLong("id"),
                lineId, stationId);

        return Optional.ofNullable(DataAccessUtils.singleResult(id));
    }

    @Override
    public int findDistanceById(Long id) {
        String sql = "SELECT distance FROM \"SECTION\" WHERE id = (?)";
        return jdbcTemplate.queryForObject(sql, Integer.class, id);
    }

    @Override
    public Long findLineOrderById(Long id) {
        String sql = "SELECT line_order FROM \"SECTION\" WHERE id = (?)";
        return jdbcTemplate.queryForObject(sql, Long.class, id);
    }

    @Override
    public void updateLineOrder(Long lineId, Long lineOrder) {
        String sql = "UPDATE \"SECTION\" SET line_order = line_order + 1 WHERE line_id = (?) AND line_order >= (?)";
        jdbcTemplate.update(sql, lineId, lineOrder);
    }
}
