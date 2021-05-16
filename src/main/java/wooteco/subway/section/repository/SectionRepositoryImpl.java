package wooteco.subway.section.repository;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.section.model.Section;
import wooteco.subway.station.dao.StationDao;

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
    public List<Section> findSectionsByLineId(Long id) {
        String sql = "SELECT id, line_id, up_station_id, down_station_id, distance"
            + " FROM section"
            + " WHERE line_id = ?";
        return jdbcTemplate.query(sql, mapperToSection(), id);
    }

    private RowMapper<Section> mapperToSection() {
        return (rs, rowNum) -> Section.builder()
            .id(rs.getLong("id"))
            .line(lineDao.findLineById(rs.getLong("line_id"))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 Line ID입니다.")))
            .upStation(stationDao.findStationById(rs.getLong("up_station_id")))
            .downStation(stationDao.findStationById(rs.getLong("down_station_id")))
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
