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
        if (sections.isRequireUpdate(section)) {
            sections.validateSectionDistance(section);
            sectionDao.update(sections.getUpdatedSection(section));
        }
        return sectionDao.save(section);
    }

    public List<Station> findStationsByLineId(Long lineId) {
        Sections sections = new Sections(findAllByLineId(lineId));
        List<Long> stationIds = sections.findStationIds();
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .filter(station -> stationIds.contains(station.getId()))
                .collect(Collectors.toList());
    }

    public List<Section> findAllByLineId(Long lineId) {
        return sectionDao.findAllByLineId(lineId);
    }
}
