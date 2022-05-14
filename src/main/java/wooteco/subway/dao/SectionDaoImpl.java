package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Section;

@Repository
public class SectionDaoImpl implements SectionDao{
    private final JdbcTemplate jdbcTemplate;

    public SectionDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(Section section, Long lineId) {
        final String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, section.getUpStation().getId());
            ps.setLong(3, section.getDownStation().getId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public void update(List<Section> sections) {
        final String sql = "UPDATE SECTION SET up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";
        List<Object[]> updateSections = sections.stream()
            .map(section -> new Object[] {section.getUpStation().getId(),
                section.getDownStation().getId(),
                section.getDistance(),
                section.getId()})
            .collect(Collectors.toList());
        jdbcTemplate.batchUpdate(sql, updateSections);
    }

    @Override
    public boolean delete(Long id) {
        final String sql = "DELETE FROM SECTION WHERE id = ?";
        int updateSize = jdbcTemplate.update(sql, id);
        return updateSize != 0;
    }
}
