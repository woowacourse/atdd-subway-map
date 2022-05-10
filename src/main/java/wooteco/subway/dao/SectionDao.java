package wooteco.subway.dao;

import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDao {

    private final SimpleJdbcInsert jdbcInsert;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SectionDao(DataSource dataSource) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Long save(Long lineId, Section section) {
        Map<String, Object> params = Map.of("line_id", lineId,
                "up_station_id", section.getUpStation().getId(),
                "down_station_id", section.getDownStation().getId(),
                "distance", section.getDistance());

        return jdbcInsert.executeAndReturnKey(params).longValue();
    }
}
