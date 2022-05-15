package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.DeletableSections;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionDeleteRequest;
import wooteco.subway.dto.SectionSaveRequest;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;

@Service
@Transactional
public class SectionService {

    private final LineRepository lines;
    private final SectionRepository sections;

    public SectionService(LineRepository lines, SectionRepository sections) {
        this.lines = lines;
        this.sections = sections;
    }

    public Section save(SectionSaveRequest request) {
        Section sectionForSave = new Section(request.getLineId(), request.getUpStationId(),
                request.getDownStationId(), request.getDistance());
        Line line = lines.findById(request.getLineId());
        line.getDividedSectionsFrom(new Section(sectionForSave.getId(), sectionForSave.getLine_id(),
                        sectionForSave.getUpStationId(), sectionForSave.getDownStationId(), sectionForSave.getDistance()))
                .ifPresent(sections::update);
        return sections.save(sectionForSave);
    }

    public void delete(SectionDeleteRequest request) {
        Line line = lines.findById(request.getLineId());
        DeletableSections deletableSections = new DeletableSections(
                line.findDeletableByStationId(request.getStationId()));
        deleteNearSections(deletableSections);
        deletableSections.mergeSections()
                .ifPresent(sections::save);
    }

    private void deleteNearSections(DeletableSections deletableSections) {
        for (Long id : deletableSections.getSectionIds()) {
            sections.deleteById(id);
        }
    }
}
