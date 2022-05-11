package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Repository
public class JdbcSectionDao implements SectionDao {

    private final SimpleJdbcInsert jdbcInsert;
    private final JdbcTemplate jdbcTemplate;

    public JdbcSectionDao(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section save(Section section, Long lineId) {
        Map<String, Object> param = new HashMap<>();
        param.put("line_id", lineId);
        param.put("up_station_id", section.getUpStationId());
        param.put("down_station_id", section.getDownStationId());
        param.put("distance", section.getDistance());
        param.put("index_num", 0);
        final Long id = jdbcInsert.executeAndReturnKey(param).longValue();
        return createNewObject(section, id);
    }

    private Section createNewObject(Section section, Long id) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, id);
        return section;
    }

    @Override
    public List<Section> findByLine(Long lineId) {
        String sql = "SELECT "
                + "sec.id, sec.distance, "
                + "sec.up_station_id, us.name up_station_name,"
                + "sec.down_station_id, ds.name down_station_name "
                + "FROM section AS sec "
                + "JOIN station AS us ON sec.up_station_id = us.id "
                + "JOIN station AS ds ON sec.down_station_id = ds.id "
                + "WHERE line_id = ? ORDER BY index_num";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            Long upStationId = resultSet.getLong("up_station_id");
            String upStationName = resultSet.getString("up_station_name");

            Long downStationId = resultSet.getLong("down_station_id");
            String downStationName = resultSet.getString("down_station_name");

            return new Section(
                    resultSet.getLong("id"),
                    new Station(upStationId, upStationName),
                    new Station(downStationId, downStationName),
                    resultSet.getInt("distance")
            );
        }, lineId);
    }

    @Override
    public void deleteByLine(Long lineId) {
        String sql = "DELETE FROM section WHERE line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }
}
