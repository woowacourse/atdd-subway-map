package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.section.IncludedStationException;
import wooteco.subway.exception.section.InvalidDeleteSectionException;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.StationService;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Transactional
public class SectionService {

    private final StationService stationService;
    private final SectionDao sectionDao;

    public SectionService(StationService stationService, SectionDao sectionDao) {
        this.stationService = stationService;
        this.sectionDao = sectionDao;
    }

    public void addInitial(Long lineId, Section section) {
        validateStationId(section.getUpStationId(), section.getDownStationId());
        sectionDao.save(lineId, section);
    }

    public Section add(Long lineId, SectionRequest sectionRequest) {
        validateStationId(sectionRequest.getUpStationId(), sectionRequest.getDownStationId());

        Section section = sectionRequest.toSection();
        Sections sections = new Sections(sectionDao.findByLineId(lineId));

        sections.validateAdditionalSection(section);
        Optional<Section> overlappedSection = sectionDao.findByUpOrDownId(lineId, section);

        Section newSection = sectionDao.save(lineId, section);
        overlappedSection.ifPresent(updateIntermediate(newSection));

        return newSection;
    }

    private void validateStationId(Long upStationId, Long downStationId) {
        stationService.validateId(upStationId);
        stationService.validateId(downStationId);
    }

    private Consumer<Section> updateIntermediate(Section newSection) {
        return originalSection -> {
            int newDistance = originalSection.getDistance() - newSection.getDistance();
            if (originalSection.isUpStation(newSection.getUpStationId())) {
                sectionDao.updateUpStation(originalSection.getId(), newSection.getDownStationId(), newDistance);
                return;
            }
            sectionDao.updateDownStation(originalSection.getId(), newSection.getUpStationId(), newDistance);
        };
    }

    public void delete(Long lineId, Long stationId) {
        validateRemovableSize(lineId);
        Sections sections = new Sections(sectionDao.findByLineAndStationId(lineId, stationId));
        merge(lineId, stationId, sections);
        for (Long sectionId : sections.sectionIds()) {
            sectionDao.delete(sectionId);
        }
    }

    public void deleteByLine(Long id) {
        sectionDao.deleteByLine(id);
    }

    private void validateRemovableSize(Long lineId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        if (sections.isOne()) {
            throw new InvalidDeleteSectionException();
        }
    }

    private void merge(Long lineId, Long stationId, Sections sections) {
        if (sections.isBiggerThanOne()) {
            Section mergedSection = sections.merge(stationId);
            sectionDao.save(lineId, mergedSection);
        }
    }

    @Transactional(readOnly = true)
    public List<Station> sortedStationIds(Long lineIds) {
        Sections sections = new Sections(sectionDao.findByLineId(lineIds));
        List<Long> sortedStationIds = sections.sortedStationIds();
        return sortedStationIds.stream()
            .map(stationService::findById)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public void validateNotIncludedStation(Long stationId) {
        if (sectionDao.isIncluded(stationId)) {
            throw new IncludedStationException();
        }
    }
}
