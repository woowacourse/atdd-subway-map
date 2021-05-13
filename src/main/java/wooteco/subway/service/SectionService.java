package wooteco.subway.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.notFound.LineNotFoundException;
import wooteco.subway.exception.notFound.StationNotFoundException;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SectionService {

    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;


    @Transactional
    public Section insertSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        final Station upStation =
            stationRepository.findStationById(upStationId).orElseThrow(StationNotFoundException::new);
        final Station downStation =
            stationRepository.findStationById(downStationId).orElseThrow(StationNotFoundException::new);

        final Sections sections = Sections.create(sectionRepository.findAllByLineId(lineId));

        if (sections.isEmpty()) {
            throw new LineNotFoundException();
        }

        final Section createdSection = Section.create(upStation, downStation, distance);
        sections.affectedSectionWhenInserting(createdSection).ifPresent(sectionRepository::update);
        sectionRepository.save(createdSection, lineId);

        return createdSection;
    }

    @Transactional
    public void dropSection(Long lineId, Long stationId) {
        final Sections sections = Sections.create(sectionRepository.findAllByLineId(lineId));
        stationRepository.findStationById(stationId).orElseThrow(StationNotFoundException::new);

        if (sections.isEmpty()) {
            throw new LineNotFoundException();
        }
        final Optional<Section> section = sections.affectedSectionWhenRemoving(stationId);
        sectionRepository.removeByStationId(lineId, stationId);
        section.ifPresent(sec -> sectionRepository.save(sec, lineId));
    }
}
