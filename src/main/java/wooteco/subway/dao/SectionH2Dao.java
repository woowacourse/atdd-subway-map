package wooteco.subway.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class SectionH2Dao implements SectionRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SectionH2Dao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Section> sectionRowMapper = (rs, rn) -> {
        long sectionId = rs.getLong("id");
        long lineId = rs.getLong("line_id");
//        long upStationId = rs.getLong("up_station_id");
//        long downStationId = rs.getLong("down_station_id");
        int distance = rs.getInt("distance");

        return new Section(sectionId, lineId, distance);
    };

    @Override
    public Section save(long lineId, Section section) {
        String query = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        this.jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, section.getUpStation().getId());
            ps.setLong(3, section.getDownStation().getId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        return this.findById(keyHolder.getKey().longValue());
    }

    @Override
    public Section findById(long sectionId) {
        String query = "SELECT * FROM SECTION WHERE id = ?";
        return jdbcTemplate.queryForObject(query, sectionRowMapper, sectionId);
    }

    @Override
    public List<Section> findAllByLineId(long lineId) {
        String query = "SELECT * FROM SECTION WHERE line_id = ?";
        return jdbcTemplate.query(query, sectionRowMapper, lineId);
    }

    @Override
    public Long getUpStationIdById(long id) {
        String query = "SELECT up_station_id FROM SECTION WHERE line_id = ?";
        return jdbcTemplate.queryForObject(query, Long.class, id);
    }

    @Override
    public Long getDownStationIdById(long id) {
        String query = "SELECT down_station_id FROM SECTION WHERE line_id = ?";
        return jdbcTemplate.queryForObject(query, Long.class, id);
    }
}
