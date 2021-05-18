package wooteco.subway.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.section.SectionDeletionException;
import wooteco.subway.station.StationResponse;
import wooteco.subway.station.StationService;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final StationService stationService;
    private final SectionDao sectionDao;

    public SectionService(StationService stationService, SectionDao sectionDao) {
        this.stationService = stationService;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void addInitial(Long lineId, Section section) {
        sectionDao.save(lineId, section);
    }

    @Transactional
    public Section add(Long lineId, SectionRequest sectionRequest) {
        Section section = new Section(sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        Sections sections = new Sections(sectionDao.findByLineId(lineId));

        sections.validate(section);

        return saveAndUpdate(lineId, section);
    }

    private Section saveAndUpdate(Long lineId, Section section) {
        Optional<Section> overlappedSection = sectionDao.findBySameUpOrDownId(lineId, section);
        Section newSection = sectionDao.save(lineId, section);
        overlappedSection.ifPresent(updateIntermediate(newSection));
        return newSection;
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

    @Transactional
    public void delete(Long lineId, Long stationId) {
        validateRemovableSize(lineId);
        Sections sections = new Sections(sectionDao.findByStation(lineId, stationId));
        merge(lineId, stationId, sections);
        for (Long sectionId : sections.sectionIds()) {
            sectionDao.delete(sectionId);
        }
    }

    private void validateRemovableSize(Long lineId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        if (sections.isOne()) {
            throw new SectionDeletionException();
        }
    }

    private void merge(Long lineId, Long stationId, Sections sections) {
        if (sections.isBiggerThanOne()) {
            Section mergedSection = sections.merge(stationId);
            sectionDao.save(lineId, mergedSection);
        }
    }

    public List<StationResponse> sortedStationIds(Long lineIds) {
        Sections sections = new Sections(sectionDao.findByLineId(lineIds));
        List<Long> sortedStationIds = sections.sortedStationIds();
        return sortedStationIds.stream()
                .map(stationService::findById)
                .collect(toList());
    }
}
