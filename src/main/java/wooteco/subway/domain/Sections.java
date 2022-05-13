package wooteco.subway.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = new LinkedList<>(sections);
    }

    public List<Station> sortSections() {
        List<Station> stations = new LinkedList<>();
        Station station = findFirstUpStation();
        stations.add(station);

        while (hasNext(station)) {
            station = findNextStation(station);
            stations.add(station);
        }

        return stations;
    }

    private Station findFirstUpStation() {
        List<Station> upStations = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
        List<Long> downStations = sections.stream()
            .map(section -> section.getDownStation().getId())
            .collect(Collectors.toList());

        return upStations.stream()
            .filter(station -> !downStations.contains(station.getId()))
            .findFirst()
            .orElseThrow();
    }

    private boolean hasNext(final Station station) {
        return sections.stream()
            .anyMatch(section -> section.getUpStation().getId().equals(station.getId()));
    }

    private Station findNextStation(final Station station) {
        return sections.stream()
            .filter(section -> section.getUpStation().getId().equals(station.getId()))
            .map(Section::getDownStation)
            .findFirst()
            .orElseThrow();
    }

    public List<Section> addSection(final Section section) {
        validateCreatable(section);

        if (!isFinalSection(section)) {
            addMiddleSection(section);
        }
        sections.add(section);
        return sections;
    }

    private void validateCreatable(final Section section) {
        Set<Long> stationIds = sections.stream()
            .map(s -> s.getUpStation().getId())
            .collect(Collectors.toSet());
        stationIds.addAll(sections.stream()
            .map(s -> s.getDownStation().getId())
            .collect(Collectors.toSet()));
        if ((stationIds.contains(section.getUpStation().getId()) && stationIds.contains(section.getDownStation().getId())) ||
            (!stationIds.contains(section.getUpStation().getId()) && !stationIds.contains(section.getDownStation().getId()))) {
            throw new IllegalStateException("노선 구간 내에 하나의 역만 존재해야 합니다.");
        }
    }

    private boolean isFinalSection(final Section section) {
        List<Station> stations = sortSections();
        return (stations.get(0).getId().equals(section.getDownStation().getId()) ||
        stations.get(stations.size() - 1).getId().equals(section.getUpStation().getId()));
    }

    private void addMiddleSection(final Section section) {
        if (isMiddleUpSection(section)) {
            addMiddleDownSection(section);
            return;
        }
        if (isMiddleDownSection(section)) {
            addMiddleUpSection(section);
            return;
        }

        throw new IllegalStateException("구간을 등록할 수 없습니다.");
    }

    private boolean isMiddleUpSection(final Section section) {
        return sections.stream()
            .anyMatch(s -> s.getUpStation().getId().equals(section.getUpStation().getId()));
    }

    private boolean isMiddleDownSection(final Section section) {
        return sections.stream()
            .anyMatch(s -> s.getDownStation().getId().equals(section.getDownStation().getId()));
    }

    private void addMiddleDownSection(final Section section) {
        final Section findSection = findExistSection(section);
        sections.remove(findSection);

        final int newDistance = findSection.getDistance() - section.getDistance();
        validateDistance(newDistance);

        sections.add(new Section(
            findSection.getLineId(),
            section.getDownStation(),
            findSection.getDownStation(),
            newDistance
        ));
    }

    private void addMiddleUpSection(final Section section) {
        final Section findSection = findExistSection(section);
        sections.remove(findSection);

        final int newDistance = findSection.getDistance() - section.getDistance();
        validateDistance(newDistance);

        sections.add(new Section(
            findSection.getLineId(),
            section.getUpStation(),
            findSection.getUpStation(),
            newDistance
        ));
    }

    private Section findExistSection(Section section) {
        return sections.stream()
            .filter(s -> s.getUpStation().getId().equals(section.getUpStation().getId()) ||
                s.getDownStation().getId().equals(section.getDownStation().getId()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("구간을 찾을 수 없습니다."));
    }

    private void validateDistance(int newDistance) {
        if (newDistance < 0) {
            throw new IllegalStateException("기존 구간보다 거리가 짧아야 합니다.");
        }
    }

    public List<Section> findUpdateSections(final Sections previousSections) {
        return sections.stream()
            .filter(previousSections::isUpdateSection)
            .collect(Collectors.toList());
    }

    private boolean isUpdateSection(final Section section) {
        return sections.stream()
            .noneMatch(
                s -> s.getDownStation().getId().equals(section.getDownStation().getId()) &&
                s.getUpStation().getId().equals(section.getUpStation().getId()) &&
                s.getDistance() == section.getDistance() &&
                Objects.equals(s.getLineId(), section.getLineId())
            );
    }

    public List<Section> remove(final Long stationId) {
        validateMinimumSectionSize();
        List<Section> removeSections = sections.stream()
            .filter(s -> s.getUpStation().getId().equals(stationId) ||
                s.getDownStation().getId().equals(stationId))
            .collect(Collectors.toList());

        removeSections.forEach(sections::remove);
        return removeSections;
    }

    private void validateMinimumSectionSize() {
        if (sections.size() == 1) {
            throw new IllegalStateException("구간은 2개 이상이어야 합니다.");
        }
    }

    public Optional<Section> mergeSection(final Long lineId, final Long stationId) {
        try {
            Section upSection = sections.stream()
                .filter(s -> s.getDownStation().getId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("구간을 찾을 수 없습니다."));
            Section downSection = sections.stream()
                .filter(s -> s.getUpStation().getId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("구간을 찾을 수 없습니다."));
            int newDistance = upSection.getDistance() + downSection.getDistance();

            return Optional.of(new Section(lineId, upSection.getUpStation(), downSection.getDownStation(), newDistance));
        } catch (IllegalStateException e) {
            return Optional.empty();
        }
    }
}
