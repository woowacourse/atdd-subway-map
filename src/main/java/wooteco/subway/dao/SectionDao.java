package wooteco.subway.dao;

import java.util.NoSuchElementException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import wooteco.subway.dto.section.SectionRequest;
import wooteco.subway.dto.section.SectionResponse;

@Component
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<SectionResponse> sectionRowMapper = (rs, rowNum) -> {
        var upStationId = rs.getLong("up_station_id");
        var downStationId = rs.getLong("down_station_id");
        return new SectionResponse(upStationId, downStationId);
    };

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(long lineId, SectionRequest sectionRequest) {
        var sql = "INSERT INTO section (up_station_id, down_station_id, distance, line_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(
                sql,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance(),
                lineId
        );
    }

    public void delete(Long lineId, Long sectionId) {
        var sql = "DELETE FROM section WHERE line_id = ? AND id = ?";
        var removedRowCount = jdbcTemplate.update(sql, lineId, sectionId);

        if (removedRowCount == 0) {
            throw new NoSuchElementException("[ERROR] 정보와 일치하는 구간이 없습니다.");
        }
    }

    public SectionResponse findByLineId(Long id) {
        var sql = "SELECT up_station_id, down_station_id FROM section WHERE line_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, sectionRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("[ERROR] 노선의 아이디값과 일치하는 구간이 없습니다.");
        }

    }
}
