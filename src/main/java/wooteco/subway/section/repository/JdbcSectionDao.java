package wooteco.subway.section.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.Section;
import wooteco.subway.section.dto.SectionRequest;

import java.sql.PreparedStatement;
import java.util.Objects;

@Repository
public class JdbcSectionDao {
    private JdbcTemplate jdbcTemplate;

    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Long lineId, SectionRequest sectionReq) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(con -> {
            PreparedStatement pstmt = con.prepareStatement(query, new String[]{"id"});
            pstmt.setLong(1, lineId);
            pstmt.setLong(2, sectionReq.getUpStationId());
            pstmt.setLong(3, sectionReq.getDownStationId());
            pstmt.setInt(4, sectionReq.getDistance());
            return pstmt;
        }, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Section(id, lineId, sectionReq);
    }
}
