package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDaoImpl implements SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(Section section) {
        final String sql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public List<Section> findByLineId(Long lineId) {
        final String sql = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(sql, sectionMapper(), lineId);
    }

    private RowMapper<Section> sectionMapper() {
        return (resultSet, rowNum) -> new Section(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance")
        );
    }

    @Override
    public boolean update(Long sectionId, Long downStationId, int distance) {
        final String sql = "UPDATE section SET down_station_id = ?, distance = ? where id = ?";
        int updateSize = jdbcTemplate.update(sql, downStationId, distance, sectionId);
        return updateSize != 0;
    }

    @Override
    public boolean deleteById(Long sectionId) {
        final String sql = "DELETE FROM section where id = ?";
        int updateSize = jdbcTemplate.update(sql, sectionId);
        return updateSize != 0;
    }
}
