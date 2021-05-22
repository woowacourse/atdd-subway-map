package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.SectionRequest;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.StationService;

import java.util.Optional;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public Section save(Section section) {
        return sectionDao.save(section);
    }

    public Sections findByLineId(Long lineId) {
        return sectionDao.findByLineId(lineId);
    }

    public void addSection(SectionRequest sectionRequest, Long id) {
        Section section = new Section(id, stationService.findById(sectionRequest.getUpStationId()), stationService.findById(sectionRequest.getDownStationId()), sectionRequest.getDistance());

        Sections sections = findByLineId(id);
        Section updateSection = sections.addSection(section);
        sectionDao.save(section);
        sectionDao.update(updateSection);
    }

    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = findByLineId(lineId);
        Station station = stationService.findById(stationId);

        Optional<Section> optSection = sections.findUpdateSectionAfterDelete(lineId, station);
        sectionDao.delete(lineId, station);
        optSection.ifPresent(sectionDao::save);
    }
}
