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
                    rs.getLong("up_station_id"),
                    rs.getLong("down_station_id"),
                    rs.getInt("distance")
            );


    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Long lineId, Section section) {
        String sql = "insert into section (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, lineId);
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
}
