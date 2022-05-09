package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("section")
            .usingGeneratedKeyColumns("id");
    }

    public Section save(Section section) {
        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", section.getId())
            .addValue("line_id", section.getLine().getId())
            .addValue("up_station_id", section.getUpStation().getId())
            .addValue("down_station_id", section.getDownStation().getId())
            .addValue("distance", section.getDistance());
        long id = jdbcInsert.executeAndReturnKey(params).longValue();

        return new Section(
            id,
            section.getLine(),
            section.getUpStation(),
            section.getDownStation(),
            section.getDistance()
        );
    }
}
