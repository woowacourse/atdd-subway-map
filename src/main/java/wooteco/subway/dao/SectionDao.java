package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionResponse;

@Repository
public class SectionDao {

    private static final RowMapper<SectionResponse> ROW_MAPPER = (rs, rn) -> {
        long id = rs.getLong("id");
        long lineId = rs.getLong("line_Id");
        long upStationId = rs.getLong("up_station_id");
        long downStationId = rs.getLong("down_station_id");
        int distance = rs.getInt("distance");
        return new SectionResponse(id, lineId, upStationId, downStationId, distance);
    };
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SectionResponse> findAllByLineId(Long lineId) {
        return jdbcTemplate.query(
            "SELECT id, line_id, up_station_id, down_station_id, distance FROM SECTION WHERE line_id = ?",
            ROW_MAPPER, lineId);
    }

    public Section save(Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                .prepareStatement(
                    "INSERT INTO SECTION(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)",
                    new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        return new Section(id, section);
    }

    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM SECTION");
    }
}
