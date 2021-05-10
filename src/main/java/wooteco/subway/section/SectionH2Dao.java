package wooteco.subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class SectionH2Dao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionH2Dao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section save(Long lineId, Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Section(id, section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    @Override
    public Optional<Section> findBySameUpOrDownId(Long lineId, Section newSection) {
        String sql = "SELECT * FROM SECTION WHERE (line_id=? AND up_station_id=?) OR (line_id=? AND down_station_id=?)";
        List<Section> sections = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Section section = new Section(
                            rs.getLong("id"),
                            rs.getLong("up_station_id"),
                            rs.getLong("down_station_id"),
                            rs.getInt("distance")
                    );
                    return section;
                }, lineId, newSection.getUpStationId(), lineId, newSection.getDownStationId());
        return sections.stream().findAny();
    }

    @Override
    public void updateUpStation(Long id, Long upStationId, int distance) {
        String sql = "UPDATE SECTION SET up_station_id=?, distance=? WHERE id=?";
        jdbcTemplate.update(sql, upStationId, distance, id);
    }

    @Override
    public void updateDownStation(Long id, Long downStationId, int distance) {
        String sql = "UPDATE SECTION SET down_station_id=?, distance=? WHERE id=?";
        jdbcTemplate.update(sql, downStationId, distance, id);
    }

    @Override
    public List<Section> findByStation(Long lineId, Long stationId) {
        String sql = "SELECT * FROM SECTION WHERE (line_id=? AND up_station_id=?) OR (line_id=? AND down_station_id=?)";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    Section section = new Section(
                            rs.getLong("id"),
                            rs.getLong("up_station_id"),
                            rs.getLong("down_station_id"),
                            rs.getInt("distance")
                    );
                    return section;
                }, lineId, stationId, lineId, stationId);
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM SECTION WHERE id=?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Section> findByLineId(Long lineId) {
        String sql = "SELECT * FROM SECTION WHERE line_id=?";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    Section section = new Section(
                            rs.getLong("id"),
                            rs.getLong("up_station_id"),
                            rs.getLong("down_station_id"),
                            rs.getInt("distance")
                    );
                    return section;
                }, lineId);
    }
}
