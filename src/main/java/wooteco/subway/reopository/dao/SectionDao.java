package wooteco.subway.reopository.dao;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.reopository.Entity.SectionEntity;

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

    public Long save(SectionEntity section) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", section.getId())
                .addValue("line_id", section.getLineId())
                .addValue("up_station_id", section.getUpStationId())
                .addValue("down_station_id", section.getDownStationId())
                .addValue("distance", section.getDistance());

        return jdbcInsert.executeAndReturnKey(params).longValue();
    }

    public List<SectionEntity> findByLineId(Long lineId) {
        String sql = "select * from section where line_id = ?";
        return jdbcTemplate.query(sql, mapper, lineId);
    }

    public void update(SectionEntity section) {
        String sql = "update section set up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
        Long id = section.getUpStationId();
        Long downStationId = section.getDownStationId();
        int distance = section.getDistance();
        Long sectionId = section.getId();

        jdbcTemplate.update(sql, id, downStationId, distance, sectionId);
    }

    public void deleteById(Long id) {
        String sql = "delete from section where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
