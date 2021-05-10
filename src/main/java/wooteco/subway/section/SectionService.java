package wooteco.subway.section;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.InvalidDeleteSectionException;
import wooteco.subway.exception.NoLineException;
import wooteco.subway.line.LineDao;
import wooteco.subway.station.StationResponse;
import wooteco.subway.station.StationService;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class SectionService {

    private final StationService stationService;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    private SectionService(StationService stationService, LineDao lineDao, SectionDao sectionDao) {
        this.stationService = stationService;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Section add(Long lineId, SectionRequest sectionRequest) {
        validateLineId(lineId);
        Section section = new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        Section newSection = sectionDao.save(lineId, section);
        Optional<Section> overlappedSection = sectionDao.findBySameUpOrDownId(lineId, newSection);
        overlappedSection.ifPresent(updateIntermediate(newSection));
        return newSection;
    }

    public Section add(Long lineId, Section section) {
        validateLineId(lineId);
        return sectionDao.save(lineId, section);
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
        validateLineId(lineId);
        validateRemovableSize(lineId);
        Sections sections = new Sections(sectionDao.findByStation(lineId, stationId));
        merge(lineId, stationId, sections);
        for (Long sectionId : sections.sectionIds()) {
            sectionDao.delete(sectionId);
        }
    }

    private void validateLineId(Long lineId) {
        lineDao.findById(lineId)
                .orElseThrow(NoLineException::new);
    }

    private void validateRemovableSize(Long lineId) {
        validateLineId(lineId);
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

    public List<StationResponse> sortedStationIds(Long lineIds) {
        validateLineId(lineIds);
        Sections sections = new Sections(sectionDao.findByLineId(lineIds));
        List<Long> sortedStationIds = sections.sortedStationIds();
        return sortedStationIds.stream()
                .map(stationService::findById)
                .collect(Collectors.toList());
    }
}