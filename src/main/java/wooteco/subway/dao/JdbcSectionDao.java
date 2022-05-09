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
    public boolean existByLineIdAndStationId(long lineId, long stationId) {
        String sql = "SELECT EXISTS ("
                + "SELECT * FROM \"SECTION\" WHERE line_id = (?) AND (up_station_id = (?) OR down_station_id = (?))"
                + ")";

        return jdbcTemplate.queryForObject(sql, boolean.class, lineId, stationId, stationId);
    }

    @Override
    public Optional<Integer> findDistanceByLineIdAndUpStationId(long lineId, long stationId) {
        String sql = "SELECT distance FROM \"SECTION\" WHERE line_id = (?) AND up_station_id = (?)";
        List<Integer> distance = jdbcTemplate.query(sql,
                (resultSet, rowMapper) -> resultSet.getInt("distance"),
                lineId, stationId);

        return Optional.ofNullable(DataAccessUtils.singleResult(distance));
    }
}
