package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.controller.dto.SectionCreateDto;
import wooteco.subway.line.controller.dto.SectionDto;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.SectionRepository;
import wooteco.subway.line.domain.Sections;
import wooteco.subway.line.exception.SectionException;
import wooteco.subway.station.service.StationService;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final SectionRepository sectionRepository;
    private final StationService stationService;

    public SectionService(final SectionRepository sectionDao, final StationService stationService) {
        this.sectionRepository = sectionDao;
        this.stationService = stationService;
    }

    @Transactional
    public SectionDto save(final SectionCreateDto sectionInfo) {
        final Section requestedSection = sectionInfo.toSection();

        if (sectionInfo.isOfNewLine()) {
            final Section savedSection = sectionRepository.save(requestedSection);
            return SectionDto.of(savedSection);
        }

        final Section savedSection = manageSectionsWith(requestedSection);
        return SectionDto.of(savedSection);
    }

    private Section manageSectionsWith(final Section requestedSection) {
        validateSection(requestedSection.getDownStationId(), requestedSection.getUpStationId());
        final Sections sections = sectionRepository.findAllByLineId(requestedSection.getLineId());

        sections.change(requestedSection)
                .ifPresent(sectionRepository::update);

        return sectionRepository.save(requestedSection);
    }

    public void validateSection(final Long downStationId, final Long upStationId) {
        validateDifferent(downStationId, upStationId);
        validateExistingStations(downStationId, upStationId);
    }

    private void validateDifferent(final Long downStationId, final Long upStationId) {
        if (downStationId.equals(upStationId)) {
            throw new SectionException("구간의 양 끝은 서로 다른 역이어야 합니다.");
        }
    }

    private void validateExistingStations(final Long downStationId, final Long upStationId) {
        stationService.findById(downStationId);
        stationService.findById(upStationId);
    }
//
//    @Transactional
//    public SectionDto update(final SectionCreateDto sectionInfo) {
//
//    }
//
//    @Transactional
//    public void delete(final SectionDeleteDto sectionInfo) {
//
//    }
}
