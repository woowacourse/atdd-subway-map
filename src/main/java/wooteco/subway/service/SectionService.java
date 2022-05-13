package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.repository.LineRepository;
import wooteco.subway.domain.repository.SectionRepository;
import wooteco.subway.domain.repository.StationRepository;
import wooteco.subway.service.dto.SectionRequest;
import wooteco.subway.utils.exception.DuplicatedException;
import wooteco.subway.utils.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class SectionService {
    private static final String NOT_FOUND_STATION_MESSAGE = "[ERROR] %d 식별자에 해당하는 역을 찾을수 없습니다.";
    private static final int DELETE_BETWEEN_STATION_STANDARD = 2;

    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public SectionService(SectionRepository sectionRepository, StationRepository stationRepository, LineRepository lineRepository) {
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    public void add(Long lineId, SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionRepository.findAllByLineId(lineId));
        validateDuplicate(sectionRequest, sections);
        validateNonMatchStations(sectionRequest, sections);
        Section newSection = sectionRepository.save(createMemorySection(sectionRequest, lineId));

        if (getTargetWithNotTerminal(sections, newSection).isPresent()) {
            Section originSection = getTargetWithNotTerminal(sections, newSection).get();
            validateDistance(sectionRequest, originSection);
            saveSection(lineId, newSection, originSection);
            sectionRepository.deleteById(originSection.getId());
        }
    }

    private void saveSection(Long lineId, Section newSection, Section section) {
        if (newSection.getUpStation().equals(section.getUpStation())) {
            sectionRepository.save(
                    new Section(lineId,
                            newSection.getDownStation(),
                            section.getDownStation(),
                            section.minusDistance(newSection.getDistance())
                    ));
        }
        if (newSection.getDownStation().equals(section.getDownStation())) {
            sectionRepository.save(
                    new Section(
                            lineId,
                            section.getUpStation(),
                            newSection.getUpStation(), section.minusDistance(newSection.getDistance())
                    ));
        }
    }

    private Optional<Section> getTargetWithNotTerminal(Sections sections, Section section) {
        return sections.findTargetWithNotTerminal(section.getUpStation(), section.getDownStation());
    }

    private void validateNonMatchStations(SectionRequest sectionRequest, Sections sections) {
        if (sections.isNonMatchStations(sectionRequest.getUpStationId(), sectionRequest.getDownStationId())) {
            throw new NotFoundException("[ERROR] 노선에 존재하는 역과 일치하는 역을 찾을수 없습니다.");
        }
    }

    private void validateDuplicate(SectionRequest sectionRequest, Sections sections) {
        if (sections.isDuplicateSection(sectionRequest.getUpStationId(), sectionRequest.getDownStationId())) {
            throw new DuplicatedException("[ERROR] 이미 노선에 존재하는 구간입니다.");
        }
    }

    private void validateDistance(SectionRequest sectionRequest, Section findSection) {
        if (findSection.isEqualsAndSmallerThan(sectionRequest.getDistance())) {
            throw new IllegalArgumentException("[ERROR] 구간의 길이가 기존보다 크거나 같습니다.");
        }
    }

    public Section init(Long lineId, SectionRequest sectionRequest) {
        Section section = createMemorySection(sectionRequest, lineId);
        return sectionRepository.save(section);
    }

    private Section createMemorySection(SectionRequest sectionRequest, Long lineId) {
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_STATION_MESSAGE, sectionRequest.getUpStationId())));
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_STATION_MESSAGE, sectionRequest.getDownStationId())));
        return new Section(lineId, upStation, downStation, sectionRequest.getDistance());
    }

    public void delete(Long lineId, Long stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_STATION_MESSAGE, stationId)));
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new NotFoundException("[ERROR] 식별자에 해당하는 노선을 찾을수 없습니다."));
        Sections sections = new Sections(sectionRepository.findAllByLineId(line.getId()));

        List<Section> deleteSections = sections.delete(station);
        Section leftSection = deleteSections.get(0);
        sectionRepository.deleteById(leftSection.getId());

        if (deleteSections.size() == DELETE_BETWEEN_STATION_STANDARD) {
            Section rightSection = deleteSections.get(1);
            Section section = Section.merge(line.getId(), leftSection, rightSection);
            sectionRepository.deleteById(rightSection.getId());
            sectionRepository.save(section);
        }
    }


}
