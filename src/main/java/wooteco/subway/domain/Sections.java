package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import wooteco.subway.exception.notfound.NotFoundSectionException;

public class Sections {

    private final List<Section> values;

    public Sections(final Section... sections) {
        this(List.of(sections));
    }

    public Sections(final List<Section> sections) {
        this.values = new ArrayList<>(sections);
    }

    public List<Station> toSortedStations() {
        final LinkedList<Station> stations = new LinkedList<>();
        final Section section = findAnySection();
        addUpperStations(stations, section);
        addLowerStations(stations, section);
        return stations;
    }

    private Section findAnySection() {
        return values.stream()
                .findAny()
                .orElseThrow(NotFoundSectionException::new);
    }

    private void addUpperStations(final LinkedList<Station> stations, final Section section) {
        stations.addFirst(section.getUpStation());
        findUpperSection(section).ifPresent(value -> addUpperStations(stations, value));
    }

    private Optional<Section> findUpperSection(final Section section) {
        return values.stream()
                .filter(section::isLowerThan)
                .findFirst();
    }

    private void addLowerStations(final LinkedList<Station> stations, final Section section) {
        stations.addLast(section.getDownStation());
        findLowerSection(section).ifPresent(value -> addLowerStations(stations, value));
    }

    private Optional<Section> findLowerSection(final Section section) {
        return values.stream()
                .filter(section::isUpperThan)
                .findFirst();
    }

    public void add(final Section section) {
        validateDuplicateSection(section);
        final Optional<Section> foundSection = findSameUpOrDownStationSection(section);
        if (foundSection.isPresent()) {
            addAtMiddle(section, foundSection.get());
            return;
        }
        values.add(section);
    }

    private void validateDuplicateSection(final Section section) {
        values.stream()
                .filter(section::equals)
                .findAny()
                .ifPresent(value -> {
                    throw new IllegalArgumentException("이미 있는 구간은 추가할 수 없습니다.");
                });
    }

    private Optional<Section> findSameUpOrDownStationSection(final Section section) {
        return values.stream()
                .filter(s -> s.hasSameUpStation(section) || s.hasSameDownStation(section))
                .findFirst();
    }

    private void addAtMiddle(final Section section, final Section originSection) {
        validateDistance(section, originSection);
        values.remove(originSection);
        values.add(section);
        values.add(createNewSection(section, originSection));
    }

    private void validateDistance(final Section section, final Section originSection) {
        if (section.isSameOrLongerThan(originSection)) {
            throw new IllegalArgumentException("구간의 길이가 너무 길어 추가할 수 없습니다.");
        }
    }

    private Section createNewSection(final Section section, final Section originSection) {
        if (section.hasSameUpStation(originSection)) {
            return new Section(section.getDownStation(), originSection.getDownStation(),
                    originSection.minusDistance(section));
        }
        return new Section(originSection.getUpStation(), section.getUpStation(), originSection.minusDistance(section));
    }

    public void remove(final Long stationId) {
        final Optional<Section> upperSection = findSameDownStationIdSection(stationId);
        final Optional<Section> lowerSection = findSameUpStationIdSection(stationId);
        if (upperSection.isPresent() && lowerSection.isPresent()) {
            removeAtMiddle(upperSection.get(), lowerSection.get());
            return;
        }
        removeAtSide(upperSection, lowerSection);
    }

    private Optional<Section> findSameUpStationIdSection(final Long stationId) {
        return values.stream()
                .filter(s -> s.getUpStation().getId().equals(stationId))
                .findFirst();
    }

    private Optional<Section> findSameDownStationIdSection(final Long stationId) {
        return values.stream()
                .filter(s -> s.getDownStation().getId().equals(stationId))
                .findFirst();
    }

    private void removeAtMiddle(final Section upperSection, final Section lowerSection) {
        final Section newSection = new Section(upperSection.getUpStation(), lowerSection.getDownStation(),
                upperSection.plusDistance(lowerSection));
        values.remove(upperSection);
        values.remove(lowerSection);
        values.add(newSection);
    }

    private void removeAtSide(final Optional<Section> upperSection, final Optional<Section> lowerSection) {
        validateSizeWhenFirstOrLastSection();
        upperSection.ifPresent(values::remove);
        lowerSection.ifPresent(values::remove);
    }

    private void validateSizeWhenFirstOrLastSection() {
        if (values.size() < 2) {
            throw new IllegalArgumentException("노선에 구간은 1개 이상이어야 합니다.");
        }
    }

    public List<Section> findDifferentSections(final Sections sections) {
        return this.values.stream()
                .filter(s -> !sections.getValues().contains(s))
                .collect(Collectors.toList());
    }

    public List<Section> getValues() {
        return List.copyOf(values);
    }
}
