package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
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

    public Long save(Section section) {
        Sections sections = new Sections(findAllByLineId(section.getLineId()));
        sections.validateSectionInLine(section);
        if (sections.isRequireUpdateForSave(section)) {
            sections.validateSectionDistance(section);
            sectionDao.update(sections.getUpdatedSectionForSave(section));
        }
        return sectionDao.save(section);
    }

    public void delete(Long lineId, Long stationId) {
        Sections sections = new Sections(findAllByLineId(lineId));
        sections.validateDelete(stationId);
    }

    public List<Station> findStationsByLineId(Long lineId) {
        Sections sections = new Sections(findAllByLineId(lineId));
        List<Long> stationIds = sections.findStationIds();
        List<Station> stations = stationDao.findAll();
        return stationIds.stream()
                .map(id -> getStationById(stations, id))
                .collect(Collectors.toList());
    }

    private Station getStationById(List<Station> stations, Long id) {
        return stations.stream()
                .filter(station -> station.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지하철역을 찾을 수 없습니다."));
    }

    public List<Section> findAllByLineId(Long lineId) {
        return sectionDao.findAllByLineId(lineId);
    }
}
