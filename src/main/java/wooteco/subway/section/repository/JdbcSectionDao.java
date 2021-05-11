package wooteco.subway.section.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.Section;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.station.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class JdbcSectionDao {
    private JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationMapper = (rs, rowNum) -> new Station (
            rs.getLong("id"),
            rs.getString("name")
    );
    private final RowMapper<Section> sectionMapper = (rs, rowNum) -> new Section (
            rs.getLong("id"),
            rs.getLong("line_id"),
            rs.getLong("up_station_id"),
            rs.getLong("down_station_id"),
            rs.getInt("distance")
    );

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

    public List<Station> findStationsBy(Long stationId, Long downStationId) {
        String query = "SELECT * FROM station WHERE id in (?, ?)";
        return jdbcTemplate.query(query, stationMapper, stationId, downStationId);
    }

    public List<Section> findAllByLineId(Long lineId) {
        String query = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(query, sectionMapper, lineId);
    }
}
