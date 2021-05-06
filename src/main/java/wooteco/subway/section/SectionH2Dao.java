package wooteco.subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Objects;

@Repository
public class SectionH2Dao {

    private final JdbcTemplate jdbcTemplate;

    public SectionH2Dao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Long lineId, Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setString(2, section.getUpStationId());
            ps.setString(3, section.getDownStationId());
            ps.setLong(4, section.getDistance());

            return ps;
        }, keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Section(id, section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }
}
