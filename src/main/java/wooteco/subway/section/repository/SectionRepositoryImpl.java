package wooteco.subway.section.repository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.model.Line;
import wooteco.subway.section.model.Section;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.model.Station;

@Repository
public class SectionRepositoryImpl implements SectionRepository {

    private final JdbcTemplate jdbcTemplate;
    private final LineDao lineDao;
    private final StationDao stationDao;

    public SectionRepositoryImpl(JdbcTemplate jdbcTemplate, LineDao lineDao,
        StationDao stationDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    @Override
    public List<Section> findSectionsByLineId(Long lineId) {
        Line line = lineDao.findLineById(lineId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 Line ID입니다."));
        List<Long> distinctStations = findDistinctStationsByLineId(lineId);
        List<Station> stations = stationDao.findAllByIds(distinctStations);
        Map<Long, Station> map = stations.stream()
            .collect(Collectors.toMap(Station::getId, Function.identity()));
        String sql = "SELECT id, line_id, up_station_id, down_station_id, distance"
            + " FROM section"
            + " WHERE line_id = ?";
        return jdbcTemplate.query(sql, mapperToSection(line, map), lineId);
    }

    private List<Long> findDistinctStationsByLineId(Long lineId) {
        List<Long> findStations = findUpStationIdsByLineId(lineId);
        findStations.addAll(findDownStationIdsByLineId(lineId));
        List<Long> distinctStations = findStations.stream()
            .distinct()
            .collect(Collectors.toList());
        return distinctStations;
    }

    private List<Long> findUpStationIdsByLineId(Long lineId) {
        String sql = "SELECT up_station_id"
            + " FROM section"
            + " WHERE line_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, lineId);
    }

    private List<Long> findDownStationIdsByLineId(Long lineId) {
        String sql = "SELECT down_station_id"
            + " FROM section"
            + " WHERE line_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, lineId);
    }

    private RowMapper<Section> mapperToSection(Line line, Map<Long, Station> stations) {
        return (rs, rowNum) -> Section.builder()
            .id(rs.getLong("id"))
            .line(line)
            .upStation(stations.get(rs.getLong("up_station_id")))
            .downStation(stations.get(rs.getLong("down_station_id")))
            .distance(rs.getInt("distance"))
            .build();
    }

    @Override
    public void save(Section section) {
        String sql = "INSERT INTO `section` (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, section.getLineId(), section.getUpStationId(),
            section.getDownStationId(), section.getDistance());
    }

    @Override
    public void deleteAllByLineId(Long lineId) {
        String sql = "DELETE FROM section WHERE line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }

    @Override
    public void saveAll(List<Section> sections) {
        String sql = "INSERT INTO `section` (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        List<Object[]> params = sections.stream()
            .map(this::parseToSectionParams)
            .collect(Collectors.toList());
        jdbcTemplate.batchUpdate(sql, params);
    }

    private Object[] parseToSectionParams(Section section) {
        return new Object[]{section.getLineId(), section.getUpStationId(),
            section.getDownStationId(),
            section.getDistance()};
    }

}
