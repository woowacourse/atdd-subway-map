package wooteco.subway.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SectionDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        final String sql = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) "
            + "VALUES(:line_id, :up_station_id, :down_station_id, :distance)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("line_id", section.getLineId());
        paramSource.addValue("up_station_id", section.getUpStation().getId());
        paramSource.addValue("down_station_id", section.getDownStation().getId());
        paramSource.addValue("distance", section.getDistance());

        jdbcTemplate.update(sql, paramSource, keyHolder);
        return new Section(keyHolder.getKey().longValue(), section.getLineId(), section.getUpStation(), section.getDownStation(),
            section.getDistance());
    }
}
