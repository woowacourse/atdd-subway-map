package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
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
        return new Section(keyHolder.getKey().longValue(), section.getLineId(),
            section.getUpStation(), section.getDownStation(),
            section.getDistance());
    }

    public boolean existByLineId(Long lineId) {
        final String sql = "SELECT EXISTS (SELECT 1 FROM section WHERE line_id = :line_id)";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("line_id", lineId);

        return jdbcTemplate.queryForObject(sql, paramSource, Integer.class) != 0;
    }

    public void deleteByLineId(Long lineId) {
        final String sql = "DELETE FROM section WHERE line_id = :line_id";

        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("line_id", lineId);

        jdbcTemplate.update(sql, paramSource);
    }

    public void saveSections(Line line) {
        final String sql = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) "
            + "VALUES(:line_id, :up_station_id, :down_station_id, :distance)";

        List<Section> sections = line.getSections().getSections();
        List<MapSqlParameterSource> params = new ArrayList<>();
        for (Section section : sections) {
            MapSqlParameterSource source = new MapSqlParameterSource();
            source.addValue("line_id", line.getId());
            source.addValue("up_station_id", section.getUpStation().getId());
            source.addValue("down_station_id", section.getDownStation().getId());
            source.addValue("distance", section.getDistance());
            params.add(source);
        }

        jdbcTemplate.batchUpdate(sql, params.toArray(MapSqlParameterSource[]::new));
    }
}
