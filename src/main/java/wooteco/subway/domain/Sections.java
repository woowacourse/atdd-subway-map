package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {

    private static final int MINIMUM_SIZE = 1;
    private static final int NEEDS_MERGE_SIZE = 2;

    private final List<Section> value;

    public Sections(final List<Section> value) {
        this.value = new ArrayList<>(value);
    }

    public void add(final Section section) {
        validateCanAdd(section);
        validateSection(section);

        findUpSection(section).ifPresent(it -> update(section, it));
        findDownSection(section).ifPresent(it -> update(section, it));
        value.add(section);
    }

    private void update(final Section source, final Section target) {
        value.remove(target);
        value.add(target.createSectionInBetween(source));
    }

    public List<Section> pop(final long stationId) {
        final List<Section> sections = value.stream()
                .filter(section -> section.getUpStation().getId() == stationId ||
                        section.getDownStation().getId() == stationId)
                .collect(Collectors.toList());
        validateMinimumSize();
        validateSectionNotFound(sections);

        value.removeAll(sections);

        if (sections.size() != NEEDS_MERGE_SIZE) {
            return sections;
        }

        mergeSections(sections);
        return sections;
    }

    private void validateSectionNotFound(final List<Section> sections) {
        if (sections.size() == 0) {
            throw new IllegalArgumentException("구간에 존재하지 않는 지하철 역입니다.");
        }
    }

    private void validateMinimumSize() {
        if (value.size() <= MINIMUM_SIZE) {
            throw new IllegalArgumentException("구간이 " + value.size() + "개 이므로 삭제할 수 없습니다.");
        }
    }

    private void mergeSections(final List<Section> sections) {
        final Section section1 = sections.get(0);
        final Section section2 = sections.get(1);

        if (section1.getDownStation().equals(section2.getUpStation())) {
            value.add(section1.merge(section2));
        }
        if (section1.getUpStation().equals(section2.getDownStation())) {
            value.add(section2.merge(section1));
        }
    }

    public Optional<Section> findMergedSection(final List<Section> sections) {
        final Section section1 = sections.get(0);
        final Section section2 = sections.get(1);

        if (section1.getDownStation().equals(section2.getUpStation())) {
            return findSection(section1.merge(section2));
        }
        if (section1.getUpStation().equals(section2.getDownStation())) {
            return findSection(section2.merge(section1));
        }
        return Optional.empty();
    }

    private Optional<Section> findSection(final Section section) {
        return value.stream()
                .filter(it -> it.equals(section))
                .findAny();
    }

    public boolean isBranched(final Section other) {
        final Optional<Section> upSection = findUpSection(other);
        final Optional<Section> downSection = findDownSection(other);
        return upSection.isPresent() || downSection.isPresent();
    }

    public Section findLastInsert() {
        return value.get(value.size() - 2);
    }

    private void validateSection(final Section other) {
        validateUpSection(other);
        validateDownSection(other);
    }

    private void validateDownSection(final Section other) {
        final Optional<Section> downSection = findDownSection(other);

        downSection.ifPresent(it -> validateDistance(it, other));
    }

    private Optional<Section> findDownSection(final Section other) {
        return value.stream()
                .filter(it -> it.getDownStation().equals(other.getDownStation()))
                .findAny();
    }

    private Optional<Section> findDownSection(final long stationId) {
        return value.stream()
                .filter(section -> section.getDownStation().getId() == stationId)
                .findAny();
    }

    private Optional<Section> findUpSection(final Section other) {
        return value.stream()
                .filter(it -> it.getUpStation().equals(other.getUpStation()))
                .findAny();
    }

    private Optional<Section> findUpSection(final long stationId) {
        return value.stream()
                .filter(section -> section.getUpStation().getId() == stationId)
                .findAny();
    }

    private void validateUpSection(final Section other) {
        final Optional<Section> upSection = findUpSection(other);

        upSection.ifPresent(it -> validateDistance(it, other));
    }

    private void validateDistance(final Section section, final Section other) {
        if (other.isGreaterOrEqualTo(section)) {
            throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 구간 거리보다 적어야 합니다.");
        }
    }

    private void validateCanAdd(final Section other) {
        final List<Station> stations = collectExistingStations();
        validateSectionInsertion(other, stations);
    }

    private List<Station> collectExistingStations() {
        return Stream.concat(getStation(Section::getUpStation), getStation(Section::getDownStation))
                .distinct()
                .collect(Collectors.toList());
    }

    private Stream<Station> getStation(Function<Section, Station> getSectionStationFunction) {
        return value.stream()
                .map(getSectionStationFunction);
    }

    private void validateSectionInsertion(final Section other, final List<Station> stations) {
        final boolean hasUpStation = stations.contains(other.getUpStation());
        final boolean hasDownStation = stations.contains(other.getDownStation());

        if (hasUpStation && hasDownStation) {
            throw new IllegalStateException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
        }
        if (!hasUpStation && !hasDownStation) {
            throw new IllegalStateException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 구간을 추가할 수 없습니다.");
        }
    }

    public List<Section> getSections() {
        return value;
    }
}
