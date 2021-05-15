package wooteco.subway.line.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.dto.SectionRequest;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class SectionRepository {
    private static final int NO_EXIST_COUNT = 0;

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Long> stationIdRowMapperByUpStationId = (resultSet, rowNum) -> resultSet.getLong("up_station_id");
    private final RowMapper<Long> stationIdRowMapperByDownStationId = (resultSet, rowNum) -> resultSet.getLong("down_station_id");
    private final RowMapper<Section> sectionRowMapperByLineId = (resultSet, rowNum) ->
            new Section(
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance")
            );

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

    public List<Section> getSectionsByLineId(final Long id) {
        String query = "SELECT up_station_id, down_station_id, distance FROM section WHERE line_id = ?";
        return jdbcTemplate.query(query, sectionRowMapperByLineId, id);
    }


    // TODO : 여기서 제거, sections 일급컬렉션에서 할작업
    public List<Long> getStationIdsByLineId(final Long id) {
        String query = "SELECT up_station_id FROM section WHERE line_id = ?";
        Set<Long> stationIds = new HashSet<>(jdbcTemplate.query(query, stationIdRowMapperByUpStationId, id));

        query = "SELECT down_station_id FROM section WHERE line_id = ?";
        stationIds.addAll(jdbcTemplate.query(query, stationIdRowMapperByDownStationId, id));
        return new ArrayList<>(stationIds);
    }

    //TODO : 로직 윗단계로 올리기
    public void saveBaseOnUpStation(final Long lineId, final SectionRequest sectionRequest) {
        try {
            String query = "SELECT down_station_id FROM section WHERE line_id = ? AND up_station_id = ?";
            Long beforeConnectedStationId = jdbcTemplate.queryForObject(query, Long.class, lineId, sectionRequest.getUpStationId());
            saveSectionBetweenStationsBaseOnUpStation(lineId, sectionRequest, beforeConnectedStationId);
        } catch (EmptyResultDataAccessException e) {
            save(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        }
    }

    //TODO : 로직 윗단계로 올리기
    private void saveSectionBetweenStationsBaseOnUpStation(final Long lineId, final SectionRequest sectionRequest, final Long beforeConnectedStationId) {
        String query = "SELECT distance FROM section WHERE line_id = ? AND up_station_id = ?";
        int beforeDistance = jdbcTemplate.queryForObject(query, Integer.class, lineId, sectionRequest.getUpStationId());
        if (beforeDistance <= sectionRequest.getDistance()) {
            throw new IllegalArgumentException("기존에 존재하는 구간의 길이가 더 짧습니다.");
        }
        sectionUpdateBetweenSaveBaseOnUpStation(lineId, sectionRequest, beforeConnectedStationId, beforeDistance);
    }

    //TODO : 로직 윗단계로 올리기
    private void sectionUpdateBetweenSaveBaseOnUpStation(final Long lineId, final SectionRequest sectionRequest, final Long beforeConnectedStationId, final int beforeDistance) {
        save(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        save(lineId, sectionRequest.getDownStationId(), beforeConnectedStationId, beforeDistance - sectionRequest.getDistance());
        delete(lineId, sectionRequest.getUpStationId(), beforeConnectedStationId);
    }

    public void saveBaseOnDownStation(final Long lineId, final SectionRequest sectionRequest) {
        try {
            String query = "SELECT up_station_id FROM section WHERE line_id = ? AND down_station_id = ?";
            Long beforeConnectedStationId = jdbcTemplate.queryForObject(query, Long.class, lineId, sectionRequest.getDownStationId());
            saveSectionBetweenStationsBaseOnDownStation(lineId, sectionRequest, beforeConnectedStationId);
        } catch (EmptyResultDataAccessException e) {
            save(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        }
    }

    private void saveSectionBetweenStationsBaseOnDownStation(final Long lineId, final SectionRequest sectionRequest, final Long beforeConnectedStationId) {
        String query = "SELECT distance FROM section WHERE line_id = ? AND down_station_id = ?";
        int beforeDistance = jdbcTemplate.queryForObject(query, Integer.class, lineId, sectionRequest.getDownStationId());
        if (beforeDistance <= sectionRequest.getDistance()) {
            throw new IllegalArgumentException("기존에 존재하는 구간의 길이가 더 짧습니다.");
        }
        sectionUpdateBetweenSaveBaseOnDownStation(lineId, sectionRequest, beforeConnectedStationId, beforeDistance);
    }

    private void sectionUpdateBetweenSaveBaseOnDownStation(final Long lineId, final SectionRequest sectionRequest, final Long beforeConnectedStationId, final int beforeDistance) {
        save(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        delete(lineId, beforeConnectedStationId, sectionRequest.getDownStationId());
        save(lineId, beforeConnectedStationId, sectionRequest.getUpStationId(), beforeDistance - sectionRequest.getDistance());
    }

    private void delete(final Long lineId, final Long upStationId, final Long downStationId) {
        String query = "DELETE FROM section WHERE line_id = ? AND up_station_id = ? AND down_station_id = ?";
        int affectedRowCount = jdbcTemplate.update(query, lineId, upStationId, downStationId);
        if (affectedRowCount == NO_EXIST_COUNT) {
            throw new NotFoundException("존재하지 않는 구간을 삭제하려 했습니다.");
        }
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        String query = "SELECT EXISTS(SELECT * FROM section WHERE line_id = ? AND up_station_id = ?)";
        if (jdbcTemplate.queryForObject(query, Boolean.class, lineId, stationId)) {
            deleteSectionBaseOnUpStation(lineId, stationId);
            return;
        }
        deleteSectionBaseOnDownStation(lineId, stationId);
    }

    private void deleteSectionBaseOnUpStation(final Long lineId, final Long stationId) {
        String query = "SELECT down_station_id FROM section WHERE line_id = ? AND up_station_id = ?";
        Long backStationId = jdbcTemplate.queryForObject(query, Long.class, lineId, stationId);
        try {
            query = "SELECT up_station_id FROM section WHERE line_id = ? AND down_station_id = ?";
            Long frontStationId = jdbcTemplate.queryForObject(query, Long.class, lineId, stationId);
            int connectDistance = getBetweenDistance(lineId, stationId);
            sectionUpdateWhenBetweenDelete(lineId, stationId, backStationId, frontStationId, connectDistance);
        } catch (EmptyResultDataAccessException e) {
            delete(lineId, stationId, backStationId);
        }
    }

    private void deleteSectionBaseOnDownStation(final Long lineId, final Long stationId) {
        String query = "SELECT up_station_id FROM section WHERE line_id = ? AND down_station_id = ?";
        Long frontStationId = jdbcTemplate.queryForObject(query, Long.class, lineId, stationId);
        delete(lineId, frontStationId, stationId);
    }

    private void sectionUpdateWhenBetweenDelete(final Long lineId, final Long stationId, final Long backStationId, final Long frontStationId, final int connectDistance) {
        delete(lineId, frontStationId, stationId);
        delete(lineId, stationId, backStationId);
        save(lineId, frontStationId, backStationId, connectDistance);
    }

    private int getBetweenDistance(final Long lineId, final Long stationId) {
        String query = "SELECT distance FROM section WHERE line_id = ? AND up_station_id = ?";
        int distance = jdbcTemplate.queryForObject(query, Integer.class, lineId, stationId);

        query = "SELECT distance FROM section WHERE line_id = ? AND down_station_id = ?";
        return distance + jdbcTemplate.queryForObject(query, Integer.class, lineId, stationId);
    }
}
