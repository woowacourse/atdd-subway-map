package wooteco.subway.section.repository;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.section.domain.Section;

@Repository
public class SectionRepository {

    private final JdbcTemplate jdbcTemplate;

    public SectionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {

        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement prepareStatement = con.prepareStatement(sql, new String[]{"id"});
            prepareStatement.setLong(1, section.getLineId());
            prepareStatement.setLong(2, section.getUpStationId());
            prepareStatement.setLong(3, section.getDownStationId());
            prepareStatement.setInt(4, section.getDistance());
            return prepareStatement;
        }, keyHolder);

        return createNewObject(section, Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    private Section createNewObject(Section section, Long id) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, id);
        return section;
    }

}
