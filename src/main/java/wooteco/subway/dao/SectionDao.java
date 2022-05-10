package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.entity.SectionEntity;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(List<Section> sections, Long lineId) {
        final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("section").usingGeneratedKeyColumns("id");

        for (Section section : sections) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("line_id", lineId);
            parameters.put("up_station_id", section.getUpStation().getId());
            parameters.put("down_station_id", section.getDownStation().getId());
            parameters.put("distance", section.getDistance());

            simpleJdbcInsert.execute(parameters);
        }
    }

    public List<SectionEntity> findByLineId(Long lineId) {
        final String sql = "SELECT * FROM section where line_id = ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> {
            return new SectionEntity(rs.getLong("id"), rs.getLong("line_id"), rs.getLong("up_station_id"), rs.getLong("down_station_id"),
                    rs.getInt("distance"));
        }), lineId);
    }

    public List<Section> findByLineId2(Long lineId) {
        final String sql = "select s.id sid, s.distance sdistance, us.id usid, us.name usname, ds.id dsid, ds.name dsname " +
                "from section s " +
                "join station us on s.up_station_id = us.id " +
                "join station ds on s.down_station_id = ds.id " +
                "where line_id = ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> {
            return new Section(rs.getLong("sid"), new Station(rs.getLong("usid"), rs.getString("usname")),
                    new Station(rs.getLong("dsid"), rs.getString("dsname")), rs.getInt("sdistance"));
        }), lineId);

//        return jdbcTemplate.query(sql, ((rs, rowNum) -> {
//            return new SectionEntity(rs.getLong("id"), rs.getLong("line_id"), rs.getLong("up_station_id"), rs.getLong("down_station_id"),
//                    rs.getInt("distance"));
//        }), lineId);
    }


    public void deleteByLineId(Long lineId) {
        final String sql = "delete from section where line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }
}
