package wooteco.subway.line.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}
