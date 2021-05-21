package wooteco.subway.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.line.LineNonexistenceException;
import wooteco.subway.exception.section.SectionDeletionException;

import java.util.Optional;
import java.util.function.Consumer;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public Section add(Long lineId, SectionRequest sectionRequest) {
        validateExistence(lineId);

        Section section = sectionRequest.toSection();
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
        validateExistence(lineId);
        validateRemovableSize(lineId);

        Sections sections = new Sections(sectionDao.findByStation(lineId, stationId));
        merge(lineId, stationId, sections);
        for (Long sectionId : sections.sectionIds()) {
            sectionDao.delete(sectionId);
        }
    }

    private void validateExistence(Long lineId) {
        sectionDao.existsByLineId(lineId)
                .orElseThrow(LineNonexistenceException::new);
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
}
