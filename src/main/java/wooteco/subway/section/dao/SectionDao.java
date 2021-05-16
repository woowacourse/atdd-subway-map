package wooteco.subway.section.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.SectionRepository;
import wooteco.subway.section.domain.Sections;

@Repository
public class SectionDao implements SectionRepository {

    private final JdbcTemplate jdbcTemplate;
    private final KeyHolder keyHolder = new GeneratedKeyHolder();

    private RowMapper<Section> rowMapper = (rs, rn) ->
            new Section(
                    rs.getLong("id"),
                    rs.getLong("line_id"),
                    rs.getLong("up_station_id"),
                    rs.getLong("down_station_id"),
                    rs.getInt("distance")
            );

    public SectionDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section save(final Section section) {
        final String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(con -> {
            final PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);
        final Long id = keyHolder.getKeyAs(Long.class);
        return new Section(id, section.getLineId(), section.getDownStationId(), section.getUpStationId(), section.getDistance());
    }

    @Override
    public Sections findAllByLineId(final Long lineId) {
        final String sql = "SELECT * FROM SECTION WHERE line_id = ?";
        final List<Section> foundSections = jdbcTemplate.query(sql, rowMapper, lineId);
        return new Sections(foundSections);
    }

    @Override
    public void update(final Section section) {
        final String sql = "UPDATE SECTION SET up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";
        jdbcTemplate.update(sql, section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getLineId());
    }

    @Override
    public void delete(final Long id) {
        final String sql = "DELETE FROM SECTION WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void deleteByStationId(Long stationId) {
        final String sql = "DELETE FROM SECTION WHERE up_station_id = ? OR down_station_id = ?";
        jdbcTemplate.update(sql, stationId, stationId);
    }
}
