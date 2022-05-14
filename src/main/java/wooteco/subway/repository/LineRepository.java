package wooteco.subway.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineDto;
import wooteco.subway.dto.SectionDto;

@Repository
public class LineRepository {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineRepository(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Long save(Line line) {
        LineDto savedLine = lineDao.save(LineDto.from(line));
        List<SectionDto> sectionDtos = line.getSections().stream()
                .map(section -> SectionDto.of(section, savedLine.getId()))
                .collect(Collectors.toList());
        sectionDao.saveAll(sectionDtos);
        return savedLine.getId();
    }

    public Line findById(Long id) {
        LineDto lineDto = lineDao.findById(id);
        List<SectionDto> sectionDtos = sectionDao.findByLineId(id);
        List<Station> stations = stationDao.findByIdIn(collectStationIds(sectionDtos));
        Sections sections = buildSections(sectionDtos, stations);
        return new Line(lineDto.getId(), lineDto.getName(), lineDto.getColor(), sections);
    }

    private Sections buildSections(List<SectionDto> sectionDtos, List<Station> stations) {
        Map<Long, Station> allStations = new HashMap<>();
        stations.forEach(station -> allStations.put(station.getId(), station));
        List<Section> sections = sectionDtos.stream()
                .map(sectionDto -> new Section(sectionDto.getId(),
                        allStations.get(sectionDto.getUpStationId()),
                        allStations.get(sectionDto.getDownStationId()),
                        sectionDto.getDistance()))
                .collect(Collectors.toList());
        return new Sections(sections);
    }

    private List<Long> collectStationIds(List<SectionDto> sectionDtos) {
        List<Long> stationIds = new ArrayList<>();
        for (SectionDto sectionDto : sectionDtos) {
            stationIds.add(sectionDto.getUpStationId());
        }
        stationIds.add(sectionDtos.get(sectionDtos.size() - 1).getDownStationId());
        return stationIds;
    }

    public List<Line> findAll() {
        return lineDao.findAll().stream()
                .map(lineDto -> findById(lineDto.getId()))
                .collect(Collectors.toList());
    }

    public void update(Line line) {
        lineDao.update(LineDto.from(line));
    }

    public void delete(Long id) {
        lineDao.deleteById(id);
    }
}
