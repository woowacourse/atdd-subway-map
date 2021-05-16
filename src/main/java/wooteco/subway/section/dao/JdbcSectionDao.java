package wooteco.subway.section.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.Section;
import wooteco.subway.section.dto.SectionResponse;

@Repository
public class JdbcSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SectionMapper mapper2;

    public JdbcSectionDao(JdbcTemplate jdbcTemplate, SectionMapper mapper,
        SectionMapper mapper2) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper2 = mapper2;
    }

    @Override
    public Section save(Long lineId, Section section) {
        String query = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values (?, ? , ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((con) -> {
            PreparedStatement ps = con.prepareStatement(query, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, section.getUpStation().getId());
            ps.setLong(3, section.getDownStation().getId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);
        return new Section(keyHolder.getKey().longValue(),
            section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    @Override
    public List<SectionResponse> findSectionsByLineId(Long lineId) {
        String query = "select * from SECTION where line_id = ?";
        return jdbcTemplate.query(query, mapper2, lineId);
    }

    @Override
    public List<SectionResponse> findById(Long lineId, Long id) {
        String query = "select * from SECTION where line_id = ? and (up_station_id =? or down_station_id =?)";
        return jdbcTemplate.query(query, mapper2, lineId, id, id);
    }


    @Override
    public void deleteByStationId(Long lineId, Long id) {
        String query = "delete from SECTION where line_id = ? and (up_station_id = ? or down_station_id = ?)";
        jdbcTemplate.update(query, lineId, id, id);
    }

    @Override
    public void deleteAllById(Long id) {
        String query = "delete from SECTION where line_id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public void deleteById(Long id) {
        String query = "delete from SECTION where id = ?";
        jdbcTemplate.update(query, id);
    }
}
