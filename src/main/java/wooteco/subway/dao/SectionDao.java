package wooteco.subway.dao;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Repository
public class SectionDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public SectionDao(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public Long save(Section section) {
        final SqlParameterSource sqlParameter = new MapSqlParameterSource(
                "line_id", section.getLineId())
                .addValue("up_station_id", section.getUpStation().getId())
                .addValue("down_station_id", section.getDownStation().getId())
                .addValue("distance", section.getDistance());

        return simpleJdbcInsert.executeAndReturnKey(sqlParameter).longValue();
    }

    public Section findById(Long id) {
        String sql = "SELECT s.id AS section_id, s.line_id, s.up_station_id, s.down_station_id, s.distance,"
                + " us.name AS up_station_name, ds.name AS down_station_name"
                + " FROM section AS s"
                + " LEFT JOIN station AS us ON us.id = s.up_station_id"
                + " LEFT JOIN station AS ds ON ds.id = s.down_station_id"
                + " WHERE s.id = :id";

        final SqlParameterSource sqlParameter = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.queryForObject(sql, sqlParameter, rowMapper());
    }

    public List<Section> findByLineId(Long lineId) {
        String sql = "SELECT s.id AS section_id, s.line_id, s.up_station_id, s.down_station_id, s.distance,"
                + " us.name AS up_station_name, ds.name AS down_station_name"
                + " FROM section AS s"
                + " LEFT JOIN station AS us ON us.id = s.up_station_id"
                + " LEFT JOIN station AS ds ON ds.id = s.down_station_id"
                + " WHERE s.line_id = :id";

        final MapSqlParameterSource sqlParameter = new MapSqlParameterSource("id", lineId);
        return namedParameterJdbcTemplate.query(sql, sqlParameter, rowMapper());
    }

    public void update(Section section) {
        String sql = "UPDATE section SET "
                + " up_station_id = :upStationId, down_station_id = :downStationId, distance = :distance"
                + " WHERE id = :id";

        SqlParameterSource sqlParameter = new MapSqlParameterSource("upStationId", section.getUpStation().getId())
                .addValue("downStationId", section.getDownStation().getId())
                .addValue("distance", section.getDistance())
                .addValue("id", section.getId());

        namedParameterJdbcTemplate.update(sql, sqlParameter);
    }

    public void deleteSections(List<Section> sections) {
        String sql = "DELETE FROM section WHERE id = :id";

        final SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(sections);
        namedParameterJdbcTemplate.batchUpdate(sql, batch);
    }

    private RowMapper<Section> rowMapper() {
        return ((rs, rowNum) -> {
            Long id = rs.getLong("section_id");
            Long lineId = rs.getLong("line_id");
            Long upStationId = rs.getLong("up_station_id");
            Long downStationId = rs.getLong("down_station_id");
            int distance = rs.getInt("distance");
            return new Section(
                    id,
                    lineId,
                    new Station(upStationId, rs.getString("up_station_name")),
                    new Station(downStationId, rs.getString("down_station_name")),
                    distance
            );
        });
    }
}
