package wooteco.subway.dao;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDao {

    private static final RowMapper<SectionEntity> mapper = (rs, rowNum) -> new SectionEntity(
        rs.getLong("id"),
        rs.getLong("line_id"),
        rs.getLong("up_station_id"),
        rs.getLong("down_station_id"),
        rs.getInt("distance"));

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

    public List<SectionEntity> findByLineId(Long lineId) {
        String sql = "select * from section where line_id = ?";
        return jdbcTemplate.query(sql, mapper, lineId);
    }

    public void update(Section section) {
        String sql = "update section set up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
        Long id = section.getUpStation().getId();
        Long downStationId = section.getDownStation().getId();
        int distance = section.getDistance();
        Long sectionId = section.getId();

        jdbcTemplate.update(sql, id, downStationId, distance, sectionId);
    }

}
