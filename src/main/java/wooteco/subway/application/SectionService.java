package wooteco.subway.application;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Section;
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
        Section newSection = new Section(lineId, request.getUpStationId(),
            request.getDownStationId(), request.getDistance());

        Optional<Section> overlapSectionOptional = findOverlapSection(lineId,
            request.getUpStationId(),
            request.getDownStationId());

        if (overlapSectionOptional.isPresent()) {
            Section overlapSection = overlapSectionOptional.get();
            Section splitSection = overlapSection.split(newSection);
            sectionRepository.save(splitSection);
            sectionRepository.deleteById(overlapSection.getId());
        }

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

    public void deleteSection(Long lineId, DeleteSectionRequest request) {
        deleteSectionRequestValidator.validate(lineId, request);
        Optional<Section> prev = findPrevSection(lineId, request.getStationId());
        Optional<Section> next = findNextSection(lineId, request.getStationId());
        deleteSection(prev, next);
    }

    private void deleteSection(Optional<Section> prevOptional,
                               Optional<Section> nextOptional) {
        if (prevOptional.isPresent() && nextOptional.isPresent()) {
            Section prev = prevOptional.get();
            Section next = nextOptional.get();
            Section newSection = prev.merge(next);
            sectionRepository.save(newSection);
        }
        prevOptional.ifPresent(section -> sectionRepository.deleteById(section.getId()));
        nextOptional.ifPresent(section -> sectionRepository.deleteById(section.getId()));
    }
}
