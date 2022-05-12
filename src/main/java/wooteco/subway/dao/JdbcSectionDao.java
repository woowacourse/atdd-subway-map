package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class JdbcSectionDao {

    private JdbcTemplate jdbcTemplate;
    private RowMapper<Section> rowMapper = (rs, rowNum) ->
            new Section(
                    rs.getLong("id"),
                    rs.getLong("line_id"),
                    rs.getLong("up_station_id"),
                    rs.getLong("down_station_id"),
                    rs.getInt("distance")
            );


    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Section section) {
        String sql = "insert into section (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, section.getLineId());
            statement.setLong(2, section.getUpStationId());
            statement.setLong(3, section.getDownStationId());
            statement.setInt(4, section.getDistance());
            return statement;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }


    public List<Section> findSectionsByLineId(long lineId) {
        String sql = "select * from section where line_id = ? ";
        return jdbcTemplate.query(sql, rowMapper, lineId);
    }

    public boolean update(Long lineId, Section section) {
        String sql = "update section set up_station_id = ?, down_station_id = ? , distance = ? where line_id = ? and id = ? ";
        return jdbcTemplate.update(sql, section.getUpStationId(), section.getDownStationId(), section.getDistance(),
                lineId, section.getId()) == 1;
    }

    public void delete(long stationId, long lineId) {
        String sql = "delete from section where line_id = ? and up_station_id = ? or down_station_id =?";
        jdbcTemplate.update(sql, lineId, stationId, stationId);
    }
}
