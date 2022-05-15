package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections2;
import wooteco.subway.domain.section.SectionsManager;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.request.CreateSectionRequest;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.exception.ExceptionType;
import wooteco.subway.exception.NotFoundException;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao,
                          StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public void save(Long lineId, CreateSectionRequest request) {
        SectionsManager sectionsManager = new SectionsManager(findValidSections(lineId));
        Station upStation = findExistingStation(request.getUpStationId());
        Station downStation = findExistingStation(request.getDownStationId());
        Sections2 updatedSections = sectionsManager.save(
                new Section(upStation, downStation, request.getDistance()));

        updateSectionChanges(sectionsManager, updatedSections, lineId);
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        SectionsManager sectionsManager = new SectionsManager((findValidSections(lineId)));
        Sections2 updatedSections = sectionsManager.delete(findExistingStation(stationId));

        updateSectionChanges(sectionsManager, updatedSections, lineId);
    }

    private void updateSectionChanges(SectionsManager oldSectionsManager, Sections2 updatedSections, Long lineId) {
        for (Section deletedSection : oldSectionsManager.extractDeletedSections(updatedSections)) {
            sectionDao.delete(SectionEntity.of(lineId, deletedSection));
        }
        for (Section updatedSection : oldSectionsManager.extractNewSections(updatedSections)) {
            sectionDao.save(SectionEntity.of(lineId, updatedSection));
        }
    }

    private List<Section> findValidSections(Long lineId) {
        return sectionDao.findAllByLineId(lineId)
                .stream()
                .map(SectionEntity::toDomain)
                .collect(Collectors.toList());
    }

    private Station findExistingStation(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.STATION_NOT_FOUND))
                .toDomain();
    }
}
