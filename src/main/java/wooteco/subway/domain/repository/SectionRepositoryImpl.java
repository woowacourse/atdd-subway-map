package wooteco.subway.domain.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

import javax.sql.DataSource;

@Repository
public class SectionRepositoryImpl implements SectionRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public SectionRepositoryImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Section save(Section section) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("line_id", section.getLineId())
                .addValue("up_station_id", section.getUpStation().getId())
                .addValue("down_station_id", section.getDownStation().getId())
                .addValue("distance", section.getDistance());

        long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Section(
                id,
                section.getLineId(),
                section.getUpStation(),
                section.getDownStation(),
                section.getDistance()
        );

    }
}
