package wooteco.subway.line.section;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

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

    public void deleteByLineId(Long id) {
        String sql = "delete from SECTION where line_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Section> findByLineId(Long lineId) {
        String sql = "select * from SECTION where line_id = ?";
        return jdbcTemplate.query(sql, (resultSet, rowNumber) ->
                new Section(
                    resultSet.getLong("id"),
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance"))
            , lineId);
    }

    public void update(Long lineId, Section resultSection) {
        String sql = "update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where line_id = ?";
        jdbcTemplate.update(
            sql,
            resultSection.getUpStationId(),
            resultSection.getDownStationId(),
            resultSection.getDistance(),
            lineId);
    }
}
