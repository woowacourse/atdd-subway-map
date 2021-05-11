package wooteco.subway.section.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import wooteco.subway.section.Section;

@Component
public class JdbcSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SectionMapper mapper;

    public JdbcSectionDao(JdbcTemplate jdbcTemplate, SectionMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public Section save(Section section) {
        String query = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values (?, ? , ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((con) -> {
            PreparedStatement ps = con.prepareStatement(query, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);
        return new Section(keyHolder.getKey().longValue(), section.getLineId(),
            section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    @Override
    public List<Section> findSectionsByLineId(Long lineId) {
        String query = "select * from SECTION where line_id = ?";
        return jdbcTemplate.query(query, mapper, lineId);
    }

    @Override
    public List<Section> findById(Long lineId, Long id) {
        String query = "select * from SECTION where line_id = ? and (up_station_id =? or down_station_id =?)";
        return jdbcTemplate.query(query, mapper, lineId);
    }


    @Override
    public void deleteById(Long lineId, Long id) {
        String query = "delete from SECTION where line_id = ? and (up_station_id = ? or down_station_id = ?)";
        jdbcTemplate.update(query, lineId, id, id);
    }

    @Override
    public void deleteAllById(Long id) {
        String query = "delete from SECTION whre line_id = ?";
        jdbcTemplate.update(query, id);
    }
}
