package wooteco.subway.domain;

import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.ClientException;

import java.util.*;

public class Sections {

    private static final int MINIMUM_SIZE = 1;
    private static final int RANDOM = 0;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void validateDeleteCondition() {
        if (sections.size() <= MINIMUM_SIZE) {
            throw new ClientException("구간을 제거할 수 없습니다.");
        }
    }

    public void validateUpAndDownSameStation(SectionRequest sectionRequest) {
        if (Objects.equals(sectionRequest.getDownStationId(), sectionRequest.getUpStationId())) {
            throw new ClientException("상행역과 하행역이 같을 수 없습니다.");
        }
    }

    public void validateSaveCondition(SectionRequest sectionRequest, LineResponse line) {
        validateUpDownStation(sectionRequest, line);
        validateDistanceSection(sectionRequest);
    }

    private void validateUpDownStation(SectionRequest sectionRequest, LineResponse line) {
        Optional<Station> downStation = findExistStation(sectionRequest.getUpStationId(), line);
        Optional<Station> upStation = findExistStation(sectionRequest.getDownStationId(), line);

        if (downStation.isPresent() && upStation.isPresent()) {
            throw new ClientException("상행역과 하행역이 이미 존재하고 있습니다.");
        }
        if (downStation.isEmpty() && upStation.isEmpty()) {
            throw new ClientException("상행역과 하행역이 모두 존재하지 않습니다.");
        }
    }

    private void validateDistanceSection(SectionRequest request) {
        boolean possibleDistance = sections.stream()
                .filter(section -> section.hasSameStationId(request))
                .allMatch(section -> isPossibleDistance(request, section));

        if (!possibleDistance) {
            throw new ClientException("역과 역 사이의 거리 조건을 만족하지 않습니다.");
        }
    }

    public List<Section> linkSections() {
        List<Section> linkedSection = new ArrayList<>();
        Section targetSection = findTopUpSection();
        linkedSection.add(targetSection);

        while (nextSection(targetSection).isPresent()) {
            Optional<Section> section = nextSection(targetSection);
            linkedSection.add(section.get());
            targetSection = section.get();
        }
        return linkedSection;
    }

    public Optional<Section> nextSection(Section target) {
        return sections.stream()
                .filter(section -> Objects.equals(section.getUpStationId(), target.getDownStationId()))
                .findAny();
    }
    private boolean isPossibleDistance(SectionRequest sectionRequest, Section section) {
        return section.isPossibleDistanceCondition(sectionRequest);
    }

    public boolean isAddSectionMiddle(SectionRequest request) {
        return sections.stream()
                .anyMatch(section -> section.hasSameStationId(request));
    }

    public boolean isMiddleSection(Long stationId) {
        return findUpSection(stationId).isPresent() && findDownSection(stationId).isPresent();
    }


    private Optional<Station> findExistStation(long id, LineResponse line) {
        return line.getStations()
                .stream()
                .filter(station -> station.getId() == id)
                .findAny();
    }

    public Section findTopUpSection() {
        return findByRecursion(sections.get(RANDOM));
    }

    private Section findByRecursion(Section target) {
        Optional<Section> upSection = sections.stream()
                .filter(section -> section.getDownStationId().equals(target.getUpStationId()))
                .findAny();

        if (upSection.isPresent()) {
            return findByRecursion(upSection.get());
        }
        return target;
    }

    public Optional<Section> findDownSection(long id) {
        return sections.stream()
                .filter(section -> Objects.equals(section.getUpStationId(), id))
                .findAny();
    }

    public Optional<Section> findUpSection(long id) {
        return sections.stream()
                .filter(section -> Objects.equals(section.getDownStationId(), id))
                .findAny();
    }

    public List<Section> getSections() {
        return sections;
    }
}
