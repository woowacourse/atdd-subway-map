package wooteco.subway.domain;

import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.ClientException;

import java.util.*;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public List<Section> linkSections() {
        List<Section> linkedSection = new ArrayList<>();
        Section targetSection = findTopUpSection();
        linkedSection.add(targetSection);

        while (nextSection(targetSection).isPresent()) {
            Optional<Section> section = nextSection(targetSection);
            if (section.isPresent()) {
                linkedSection.add(section.get());
                targetSection = section.get();
            }
        }
        return linkedSection;
    }

    public void validateDeleteCondition() {
        if (sections.size() <= 1) {
            throw new ClientException("구간을 제거할 수 없습니다.");
        }
    }

    public void validateSaveCondition(SectionRequest sectionRequest, LineResponse line) {
        validateUpDownAllStation(sectionRequest, line);
        validateLengthSection(sectionRequest);
    }

    private void validateUpDownAllStation(SectionRequest sectionRequest, LineResponse line) {
        Optional<Station> downStation = findExistStation(sectionRequest.getUpStationId(), line);
        Optional<Station> upStation = findExistStation(sectionRequest.getDownStationId(), line);

        if (downStation.isPresent() && upStation.isPresent()) {
            throw new ClientException("상행역과 하행역이 이미 존재하고 있습니다.");
        }
        if (downStation.isEmpty() && upStation.isEmpty()) {
            throw new ClientException("상행역과 하행역이 모두 존재하지 않습니다.");
        }
    }

    private void validateLengthSection(SectionRequest sectionRequest) {
        for (Section section : sections) {
            if (section.isSameUpStationId(sectionRequest) || section.isSameDownStationId(sectionRequest)) {
                validateLengthException(sectionRequest, section);
            }
        }
    }

    private void validateLengthException(SectionRequest sectionRequest, Section section) {
        if (!section.isPossibleDistanceCondition(sectionRequest)) {
            throw new ClientException("역과 역 사이의 거리 조건을 만족하지 않습니다.");
        }
    }

    private Optional<Station> findExistStation(long id, LineResponse line) {
        Set<Station> stations = line.getStations();
        return stations.stream()
                .filter(station -> station.getId() == id)
                .findAny();
    }

    public boolean isAddSectionMiddle(SectionRequest sectionRequest) {
        for (Section section : sections) {
            if (section.isSameUpStationId(sectionRequest) || section.isSameDownStationId(sectionRequest)) {
                return true;
            }
        }
        return false;
    }

    public boolean isExistSection() {
        return sections.size() > 0;
    }

    public Section findTopUpSection() {
        return findByRecursion(sections.get(0));
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

    public Optional<Section> nextSection(Section target) {
        return sections.stream()
                .filter(section -> Objects.equals(section.getUpStationId(), target.getDownStationId()))
                .findAny();
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
