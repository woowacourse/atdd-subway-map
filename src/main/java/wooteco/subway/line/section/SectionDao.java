package wooteco.subway.line.section;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNumber) -> new Section(
        resultSet.getLong("id"),
        resultSet.getLong("up_station_id"),
        resultSet.getLong("down_station_id"),
        resultSet.getInt("distance")
    );

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Long lineId, Section section) {
        String sql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setLong(1, lineId);
            preparedStatement.setLong(2, section.getUpStationId());
            preparedStatement.setLong(3, section.getDownStationId());
            preparedStatement.setLong(4, section.getDistance());
            return preparedStatement;
        });
    }

    public void deleteByLineId(Long lineId) {
        String sql = "delete from SECTION where line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }

    public List<Section> findByLineId(Long lineId) {
        String sql = "select * from SECTION where line_id = ?";
        return jdbcTemplate.query(sql, sectionRowMapper, lineId);
    }

    public void update(Long lineId, Section resultSection) {
        String sql = "update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where line_id = ? and id = ?";
        jdbcTemplate.update(
            sql,
            resultSection.getUpStationId(),
            resultSection.getDownStationId(),
            resultSection.getDistance(),
            lineId,
            resultSection.getId()
        );
    }

    public Section findById(Long lineId, Long sectionId) {
        String sql = "select * from SECTION where line_id = ? and id = ?";
        return jdbcTemplate.queryForObject(sql, sectionRowMapper, lineId, sectionId);
    }

    public void deleteById(Long lineId, Long sectionId) {
        String sql = "delete from SECTION where line_id = ? and id = ?";
        jdbcTemplate.update(sql, lineId, sectionId);
    }
}
