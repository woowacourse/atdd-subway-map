package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Section;

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
        final Long id = jdbcInsert.executeAndReturnKey(param).longValue();
        return createNewObject(section, id);
    }

    private Section createNewObject(Section section, Long id) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, id);
        return section;
    }
}
