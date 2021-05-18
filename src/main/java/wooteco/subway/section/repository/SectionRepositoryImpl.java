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
import wooteco.subway.section.model.LineSection;
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
        String sql = "SELECT id, line_id, up_station_id, down_station_id, distance"
            + " FROM section"
            + " WHERE line_id = ?";

        List<LineSection> lineSections = jdbcTemplate
            .query(sql, mapperLineSection(), lineId);

        Line line = lineDao.findLineById(lineId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 Line ID입니다."));
        Map<Long, Station> stations = mapStationsById(lineSections);

        return lineSections.stream()
            .map(it -> Section.builder()
                .id(it.getId())
                .line(line)
                .upStation(stations.get(it.getUpStationId()))
                .downStation(stations.get(it.getDownStationId()))
                .distance(it.getDistance())
                .build())
            .collect(Collectors.toList());
    }

    private Map<Long, Station> mapStationsById(List<LineSection> lineSections) {
        List<Long> stationIds = findDistinctStationsByLineId(lineSections);
        List<Station> stations = stationDao.findAllByIds(stationIds);
        Map<Long, Station> map = stations.stream()
            .collect(Collectors.toMap(Station::getId, Function.identity()));
        return map;
    }

    private RowMapper<LineSection> mapperLineSection() {
        return (rs, rowNum) -> LineSection.builder()
            .id(rs.getLong("id"))
            .lineId(rs.getLong("line_id"))
            .upStationId(rs.getLong("up_station_id"))
            .downStationId(rs.getLong("down_station_id"))
            .distance(rs.getInt("distance"))
            .build();
    }

    private List<Long> findDistinctStationsByLineId(List<LineSection> lineSections) {
        List<Long> stationIds = lineSections.stream()
            .map(LineSection::getUpStationId)
            .collect(Collectors.toList());

        List<Long> downStationIds = lineSections.stream()
            .map(LineSection::getDownStationId)
            .collect(Collectors.toList());
        stationIds.addAll(downStationIds);
        return stationIds;
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
