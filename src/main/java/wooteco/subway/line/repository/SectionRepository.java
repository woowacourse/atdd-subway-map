package wooteco.subway.line.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.line.domain.Section;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class SectionRepository {
    private static final int NO_EXIST_COUNT = 0;

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Section> sectionRowMapperByLineId = (resultSet, rowNum) ->
            new Section(
                    resultSet.getLong("line_id"),
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance")
            );

    public SectionRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(final Section section) {
        String query = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ? ,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(Connection -> {
            PreparedStatement ps = Connection.prepareStatement(query, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);
    }

    public List<Section> getSectionsByLineId(final Long id) {
        String query = "SELECT line_id, up_station_id, down_station_id, distance FROM section WHERE line_id = ?";
        return jdbcTemplate.query(query, sectionRowMapperByLineId, id);
    }

    public void delete(final Long lineId, Long upStationId, Long downStationId) {
        String query = "DELETE FROM section WHERE line_id = ? AND up_station_id = ? AND down_station_id = ?";
        int affectedRowCount = jdbcTemplate.update(query, lineId, upStationId, downStationId);
        if (affectedRowCount == NO_EXIST_COUNT) {
            throw new NotFoundException("존재하지 않는 구간을 삭제하려 했습니다.");
        }
    }
}
