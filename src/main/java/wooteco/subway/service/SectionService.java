package wooteco.subway.service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionWithStation;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Service
public class SectionService {
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Section saveSection(Section section) {
        Long id = sectionDao.save(section);
        return sectionDao.findById(id);
    }

    public List<Station> findStationsOfLine(Long lineId) {
        Sections sections = new Sections(
                sectionDao.findAllByLineId(lineId)
                        .stream()
                        .map(getSectionWithStation())
                        .collect(Collectors.toList())
        );
        return sections.calculateStations();
    }

    private Function<Section, SectionWithStation> getSectionWithStation() {
        return section -> new SectionWithStation(
                section.getId(),
                section.getLineId(),
                stationDao.findById(section.getUpStationId()),
                stationDao.findById(section.getDownStationId()),
                section.getDistance()
        );
    }

    public void deleteSection(Long lineId, Long stationId) {
        List<Section> sections = sectionDao.findAllByLineId(lineId);
        boolean isUpStation = sections.stream().anyMatch(section -> section.getUpStationId().equals(stationId));
        boolean isDownStation = sections.stream().anyMatch(section -> section.getDownStationId().equals(stationId));
        //

    }
}
