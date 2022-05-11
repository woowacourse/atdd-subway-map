package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

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
        Map<String, Object> params = Map.of(
                "line_id", lineId,
                "up_station_id", section.getUpStation().getId(),
                "down_station_id", section.getDownStation().getId(),
                "distance", section.getDistance());

        return jdbcInsert.executeAndReturnKey(params).longValue();
    }

    public List<Section> findAllByLineId(Long id) {
        String sql =
                "select s.id, s.up_station_id, up.name as up_name, s.down_station_id, down.name as down_name, distance "
                        + "from section as s "
                        + "join station as up "
                        + "on s.up_station_id = up.id "
                        + "join station as down "
                        + "on s.down_station_id = down.id "
                        + "where s.line_id = :line_id";
        return jdbcTemplate.query(sql, Map.of("line_id", id), sectionRowMapper());
    }

    public void remove(Section deleteTargetSection) {
        String sql = "delete from SECTION where id = :id";
        jdbcTemplate.update(sql, Map.of("id", deleteTargetSection.getId()));
    }

    private RowMapper<Section> sectionRowMapper() {
        return (rs, rowNum) -> new Section(
                rs.getLong("id"),
                new Station(rs.getLong("up_station_id"), rs.getString("up_name")),
                new Station(rs.getLong("down_station_id"), rs.getString("down_name")),
                rs.getInt("distance")
        );
    }
}
