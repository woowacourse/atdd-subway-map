package wooteco.subway.section.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.RequestException;
import wooteco.subway.section.service.dto.SectionCreateDto;
import wooteco.subway.section.service.dto.SectionDeleteDto;
import wooteco.subway.section.service.dto.SectionDto;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.SectionRepository;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.StationService;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final SectionRepository sectionRepository;
    private final StationService stationService;

    public SectionService(final SectionRepository sectionService, final StationService stationService) {
        this.sectionRepository = sectionService;
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
            throw new RequestException("구간의 양 끝은 서로 다른 역이어야 합니다.");
        }
    }

    private void validateExistingStations(final Long downStationId, final Long upStationId) {
        stationService.findById(downStationId);
        stationService.findById(upStationId);
    }

    @Transactional
    public void delete(final SectionDeleteDto sectionInfo) {
        final Long deletingStationId = sectionInfo.getStationId();
        final Sections sections = sectionRepository.findAllByLineId(sectionInfo.getLineId());

        final List<Section> changedSections = sections.removeStation(deletingStationId);
        updateSectionsWith(changedSections, deletingStationId);
    }

    private void updateSectionsWith(final List<Section> changedSections, final Long deletingStationId) {
        if (changedSections.size() == 2) {
            final Section firstSection = changedSections.get(0);
            final Section secondSection = changedSections.get(1);
            final Section connectSection = firstSection.shortenWith(secondSection);

            sectionRepository.update(connectSection);
        }
        sectionRepository.deleteByStationId(deletingStationId);
    }

    public List<Station> orderedStationsByLineId(final Long lineId) {
        final Sections sections = sectionRepository.findAllByLineId(lineId);
        return stationService.findAllById(sections.toOrderedStationIds());
    }
}
