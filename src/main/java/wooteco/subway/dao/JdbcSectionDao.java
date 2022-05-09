package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import wooteco.subway.domain.Section;

public class JdbcSectionDao {

    public static final int FUNCTION_SUCCESS = 1;
    private final JdbcTemplate jdbcTemplate;

    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Section section) {
        String sql = "insert into section (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, section.getLineId());
            preparedStatement.setLong(2, section.getUpStationId());
            preparedStatement.setLong(3, section.getDownStationId());
            preparedStatement.setLong(4, section.getDistance());
            return preparedStatement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public boolean deleteByLineIdAndStationId(Long lineId, Long stationId) {
        String sql = "delete from section where line_id = ? and (up_station_id = ? or down_station_id = ?)";
        return jdbcTemplate.update(sql, lineId, stationId, stationId) == FUNCTION_SUCCESS;
    }
}
