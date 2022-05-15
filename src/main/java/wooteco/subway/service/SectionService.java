package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.section.SectionsFactory;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.request.CreateSectionRequest;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.exception.ExceptionType;

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
        Sections sections = SectionsFactory.generate(findValidSections(lineId));
        Station upStation = findExistingStation(request.getUpStationId());
        Station downStation = findExistingStation(request.getDownStationId());
        Sections updatedSections = sections.save(new Section(upStation, downStation, request.getDistance()));

        updateSectionChanges(sections, updatedSections, lineId);
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        Sections sections = SectionsFactory.generate(findValidSections(lineId));
        Sections updatedSections = sections.delete(findExistingStation(stationId));

        updateSectionChanges(sections, updatedSections, lineId);
    }

    private void updateSectionChanges(Sections oldSections, Sections updatedSections, Long lineId) {
        for (Section deletedSection : updatedSections.extractDeletedSections(oldSections)) {
            sectionDao.delete(deletedSection.toEntity(lineId));
        }
        for (Section deletedSection : updatedSections.extractNewSections(oldSections)) {
            sectionDao.save(deletedSection.toEntity(lineId));
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
