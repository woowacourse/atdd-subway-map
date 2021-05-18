package wooteco.subway.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.line.LineNonexistenceException;
import wooteco.subway.exception.section.SectionDeletionException;
import wooteco.subway.line.LineDao;

import java.util.Optional;
import java.util.function.Consumer;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public SectionService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public Section add(Long lineId, SectionRequest sectionRequest) {
        validateLineId(lineId);

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
        validate(lineId);

        Sections sections = new Sections(sectionDao.findByStation(lineId, stationId));
        merge(lineId, stationId, sections);
        for (Long sectionId : sections.sectionIds()) {
            sectionDao.delete(sectionId);
        }
    }

    private void validate(Long lineId) {
        validateLineId(lineId);
        validateRemovableSize(lineId);
    }

    private void validateLineId(Long lineId) {
        lineDao.findById(lineId)
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
