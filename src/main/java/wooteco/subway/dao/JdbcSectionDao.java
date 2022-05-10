package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

@Repository
public class JdbcSectionDao {

    public static final int FUNCTION_SUCCESS = 1;
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Section> rowMapper = (rs, rowNum) ->
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

    public Sections findByLineId(Long lineId) {
        String sql = "select * from section where line_id = ?";
        List<Section> sections = jdbcTemplate.query(sql, rowMapper, lineId);
        return new Sections(sections);
    }

    public Optional<Section> findByLineIdAndUpStationId(Long lineId, Long upStationId) {
        String sql = "select * from section where line_id = ? and up_station_id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, lineId, upStationId));
    }

    public Optional<Section> findByLineIdAndDownStationId(Long lineId, Long downStationId) {
        String sql = "select * from section where line_id = ? and down_station_id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, lineId, downStationId));
    }

    public boolean updateDownStationIdByLineIdAndUpStationId(Long lineId, Long upStationId, Long downStationId) {
        String sql = "update section set down_station_id = ? where line_id = ? and up_station_id = ?";
        return jdbcTemplate.update(sql, downStationId, lineId, upStationId) == FUNCTION_SUCCESS;
    }

    public boolean deleteByLineIdAndUpStationId(Long lineId, Long upStationId) {
        String sql = "delete from section where line_id = ? and up_station_id = ?";
        return jdbcTemplate.update(sql, lineId, upStationId) == FUNCTION_SUCCESS;
    }
}
