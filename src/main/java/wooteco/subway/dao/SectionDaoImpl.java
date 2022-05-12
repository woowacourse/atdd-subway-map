package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDaoImpl implements SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final RowMapper<Section> rowMapper = (rs, rowNum) ->
            new Section(
                    rs.getLong("line_id"),
                    rs.getLong("up_station_id"),
                    rs.getLong("down_station_id"),
                    rs.getInt("distance")
            );

    public SectionDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Section insert(Section section) {
        Long lineId = section.getLineId();
        Long upStationId = section.getUpStationId();
        Long downStationId = section.getDownStationId();
        Integer distance = section.getDistance();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("line_id", lineId)
                .addValue("up_station_id", upStationId)
                .addValue("down_station_id", downStationId)
                .addValue("distance", distance);

        long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        return new Section(id, lineId, upStationId, downStationId, distance);
    }

    @Override
    public List<Section> save(List<Section> sections) {
        List<Section> newSections = new ArrayList<>();
        for (Section section : sections) {
            newSections.add(insert(section));
        }
        return Collections.unmodifiableList(newSections);
    }

    @Override
    public List<Section> findByLineId(Long lineId) {
        String sql = "SELECT * FROM SECTION WHERE line_id = ?";
        return Collections.unmodifiableList(jdbcTemplate.query(sql, rowMapper, lineId));
    }

    @Override
    public void deleteByLineId(Long lineId) {
        String sql = "DELETE FROM SECTION WHERE line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }
}
