package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        String sql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());

        return createNewObject(section);
    }

    public void delete(Long stationId) {
        String sql = "delete from SECTION where up_station_id = ? or down_station_id = ?";
        jdbcTemplate.update(sql, stationId, stationId);
    }

    private Section createNewObject(Section section) {
        String sql = "select max(id) from SECTION";
        Long id = jdbcTemplate.queryForObject(sql, Long.class);
        return new Section(id, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public List<Section> findSectionIn(Line line) {
        String sql = String.format("select * from SECTION where line_id = %d", line.getId());
        return jdbcTemplate.query(sql, new SectionMapper());
    }

    public void checkValid(Section section) {
        checkDuplication(section);
        checkConnected(section);
        checkDistance(section);
    }

    private void checkDuplication(Section section) {
        String sql = String.format("select count(*) from SECTION where lineId = %d and (upStationId = %d and downStationId = %d) or (upStationId = %d and downStationId = %d)",
                section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDownStationId(), section.getUpStationId());

        if (jdbcTemplate.queryForObject(sql, Integer.class) > 0) {
            throw new IllegalArgumentException("이미 존재하는 구간 이름입니다.");
        }
    }

    private void checkConnected(Section section) {
        String sql = String.format("select count(*) from SECTION where lineId = %d and (upStationId = %d or downStationId = %d or upStationId = %d or downStationId = %d)",
                section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDownStationId(), section.getUpStationId());

        if (jdbcTemplate.queryForObject(sql, Integer.class) == 0) {
            throw new IllegalArgumentException("기존 노선과 연결된 구간이 아닙니다.");
        }
    }

    private void checkDistance(Section section) {
        String sql1 = String.format("select * from SECTION where lineId = %d and (upStationId = %d or downStationId = %d)", section.getLineId(), section.getUpStationId(), section.getDownStationId());

        List<Section> sections = jdbcTemplate.query(sql1, new SectionMapper());

        if (isAvailableDistance(section, sections.get(0))) {
            throw new IllegalArgumentException("적절한 거리가 아닙니다.");
        }
    }

    private boolean isAvailableDistance(Section newSection, Section oldSection) {
        return newSection.getDistance() > oldSection.getDistance();
    }

    private static class SectionMapper implements RowMapper<Section> {
        public Section mapRow(ResultSet rs, int rowCnt) throws SQLException {
            return new Section(rs.getLong("id"), rs.getLong("line_id"),
                    rs.getLong("up_station_id"), rs.getLong("down_station_id"),
                    rs.getInt("distance"));
        }
    }
}
