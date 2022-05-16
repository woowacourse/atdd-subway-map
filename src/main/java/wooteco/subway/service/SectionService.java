package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DataNotExistException;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    private final StationService stationService;

    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public Long save(Section section) {
        Sections sections = new Sections(findAllByLineId(section.getLineId()));
        if (!sections.isEmpty()) {
            sections.validateSectionInLine(section);
            updateSectionForSave(section, sections);
        }
        return sectionDao.save(section);
    }

    private void updateSectionForSave(Section section, Sections sections) {
        if (sections.isRequireUpdateForSave(section)) {
            sections.validateSectionDistance(section);
            sectionDao.update(sections.getUpdatedSectionForSave(section));
        }
    }

    public void delete(Long lineId, Long stationId) {
        Sections sections = new Sections(findAllByLineId(lineId));
        sections.validateDelete(stationId);
        if (sections.isRequireUpdateForDelete(stationId)) {
            sectionDao.update(sections.getUpdatedSectionForDelete(stationId));
        }
        sectionDao.delete(sections.getDeletedSectionId(stationId));
    }

    public List<Station> findStationsByLineId(Long lineId) {
        Sections sections = new Sections(findAllByLineId(lineId));
        List<Long> stationIds = sections.findStationIds();
        List<Station> stations = stationService.findAll();
        return stationIds.stream()
                .map(id -> getStationById(stations, id))
                .collect(Collectors.toList());
    }

    private Station getStationById(List<Station> stations, Long id) {
        return stations.stream()
                .filter(station -> station.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new DataNotExistException("지하철역을 찾을 수 없습니다."));
    }

    public List<Section> findAllByLineId(Long lineId) {
        return sectionDao.findAllByLineId(lineId);
    }
}
