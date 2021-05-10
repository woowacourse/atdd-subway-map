package wooteco.subway.line.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.dto.SectionRequest;

import java.sql.PreparedStatement;
import java.util.*;

@Repository
public class SectionRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Long> stationIdRowMapperByUpStationId = (resultSet, rowNum) -> resultSet.getLong("up_station_id");
    private final RowMapper<Long> stationIdRowMapperByDownStationId = (resultSet, rowNum) -> resultSet.getLong("down_station_id");


    public SectionRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        String query = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ? ,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(Connection -> {
            PreparedStatement ps = Connection.prepareStatement(query, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, upStationId);
            ps.setLong(3, downStationId);
            ps.setInt(4, distance);
            return ps;
        }, keyHolder);
    }

    public List<Long> getStationIdsByLineId(final Long id) {
        String query = "SELECT up_station_id FROM section WHERE line_id = ?";
        Set<Long> stationIds = new HashSet<>(jdbcTemplate.query(query, stationIdRowMapperByUpStationId, id));

        query = "SELECT down_station_id FROM section WHERE line_id = ?";
        stationIds.addAll(jdbcTemplate.query(query, stationIdRowMapperByDownStationId, id));
        return new ArrayList<>(stationIds);
    }

    public void saveBaseOnUpStation(final Long lineId, final SectionRequest sectionRequest) {
        // 시작점 뒤에 아무것도 없는경우
        // line_id에 있는 데이터중, 시작점을 up_station으로 가진 놈이 테이블에 있는지 확인한다.
        try {
            String query = "SELECT up_station_id FROM section WHERE line_id = ? AND up_station_id = ? ORDER BY distance DESC LIMIT 1";
            Long ClosestStationId = Objects.requireNonNull(jdbcTemplate.queryForObject(query, Long.class, lineId, sectionRequest.getUpStationId()));
        } catch (Exception e) {
            save(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        }
    }
}
