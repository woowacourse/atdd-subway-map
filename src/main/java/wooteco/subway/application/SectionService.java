package wooteco.subway.application;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionEdge;
import wooteco.subway.dto.AddSectionRequest;
import wooteco.subway.dto.DeleteSectionRequest;
import wooteco.subway.repository.SectionRepository;

@Service
@Transactional
public class SectionService {

    private final AddSectionRequestValidator addSectionRequestValidator;
    private final DeleteSectionRequestValidator deleteSectionRequestValidator;
    private final SectionRepository sectionRepository;

    public SectionService(AddSectionRequestValidator addSectionRequestValidator,
                          DeleteSectionRequestValidator deleteSectionRequestValidator,
                          SectionRepository sectionRepository) {
        this.addSectionRequestValidator = addSectionRequestValidator;
        this.deleteSectionRequestValidator = deleteSectionRequestValidator;
        this.sectionRepository = sectionRepository;
    }

    public Section addSection(Long lineId, AddSectionRequest request) {
        addSectionRequestValidator.validate(lineId, request);
        SectionEdge newEdge = new SectionEdge(request.getUpStationId(),
            request.getDownStationId(), request.getDistance());
        Section newSection = new Section(lineId, newEdge);

        findOverlapSection(lineId, request.getUpStationId(), request.getDownStationId())
            .ifPresent(section -> splitSection(newSection, section));

        return sectionRepository.save(newSection);
    }

    private Optional<Section> findOverlapSection(Long lineId, Long upStationId,
                                                 Long downStationId) {
        return findNextSection(lineId, upStationId)
            .or(() -> findPrevSection(lineId, downStationId));
    }

    private Optional<Section> findNextSection(Long lineId, Long stationId) {
        return sectionRepository.findByLineIdAndUpStationId(lineId, stationId);
    }

    private Optional<Section> findPrevSection(Long lineId, Long stationId) {
        return sectionRepository.findByLineIdAndDownStationId(lineId, stationId);
    }

    private void splitSection(Section newSection, Section overlapSection) {
        Section splitSection = overlapSection.split(newSection);
        sectionRepository.save(splitSection);
        sectionRepository.deleteById(overlapSection.getId());
    }

    public void deleteSection(Long lineId, DeleteSectionRequest request) {
        deleteSectionRequestValidator.validate(lineId, request);
        Optional<Section> prev = findPrevSection(lineId, request.getStationId());
        Optional<Section> next = findNextSection(lineId, request.getStationId());

        if (prev.isPresent() && next.isPresent()) {
            mergeSection(prev.get(), next.get());
        }

        prev.ifPresent(this::deleteSection);
        next.ifPresent(this::deleteSection);
    }

    private void deleteSection(Section section) {
        sectionRepository.deleteById(section.getId());
    }

    private void mergeSection(Section prev, Section next) {
        Section newSection = prev.merge(next);
        sectionRepository.save(newSection);
    }

}
