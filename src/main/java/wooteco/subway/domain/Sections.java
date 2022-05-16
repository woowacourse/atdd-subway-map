package wooteco.subway.domain;

import wooteco.subway.domain.exception.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {

    private static final int MINIMUM_SIZE = 1;
    private static final int NEEDS_MERGE_SIZE = 2;
    private static final int SOURCE_INDEX = 0;
    private static final int TARGET_INDEX = 1;

    private final List<Section> value;

    public Sections(final List<Section> value) {
        this.value = new ArrayList<>(value);
    }

    public void add(final Section section) {
        validateCanAdd(section);

        findUpSection(section).ifPresent(it -> update(section, it));
        findDownSection(section).ifPresent(it -> update(section, it));
        value.add(section);
    }

    public void remove(final long stationId) {
        final List<Section> sections = findSectionsByStationId(stationId);
        validateMinimumSize();
        validateSectionNotFound(sections);

        value.removeAll(sections);

        if (sections.size() == NEEDS_MERGE_SIZE) {
            final Section section = mergeSections(sections);
            value.add(section);
        }
    }

    public List<Station> extractStations() {
        return Stream.concat(getStations(Section::getUpStation), getStations(Section::getDownStation))
                .distinct()
                .collect(Collectors.toList());
    }

    private void update(final Section source, final Section target) {
        value.remove(target);
        value.add(target.createSectionInBetween(source));
    }

    private List<Section> findSectionsByStationId(final long stationId) {
        return value.stream().filter(section -> section.getUpStation().getId() == stationId || section.getDownStation().getId() == stationId).collect(Collectors.toList());
    }

    private void validateSectionNotFound(final List<Section> sections) {
        if (sections.size() == 0) {
            throw new SectionNotFoundException();
        }
    }

    private void validateMinimumSize() {
        if (value.size() <= MINIMUM_SIZE) {
            throw new IllegalSectionDeleteBySizeException();
        }
    }

    private Section mergeSections(final List<Section> sections) {
        final Section source = sections.get(SOURCE_INDEX);
        final Section target = sections.get(TARGET_INDEX);

        if (source.getDownStation().equals(target.getUpStation())) {
            return source.merge(target);
        }
        if (source.getUpStation().equals(target.getDownStation())) {
            return target.merge(source);
        }
        throw new CannotMergeException();
    }

    private void validateCanAdd(final Section other) {
        validateSectionInsertion(other, extractStations());
        validateUpSection(other);
        validateDownSection(other);
    }

    private Stream<Station> getStations(Function<Section, Station> function) {
        return value.stream()
                .map(function);
    }

    private void validateSectionInsertion(final Section other, final List<Station> stations) {
        final boolean hasUpStation = stations.contains(other.getUpStation());
        final boolean hasDownStation = stations.contains(other.getDownStation());

        if (hasUpStation && hasDownStation) {
            throw new SectionAlreadyExistsException();
        }
        if (!hasUpStation && !hasDownStation) {
            throw new NoStationExistsException();
        }
    }

    private void validateUpSection(final Section other) {
        final Optional<Section> upSection = findUpSection(other);

        upSection.ifPresent(it -> validateDistance(it, other));
    }

    private void validateDownSection(final Section other) {
        final Optional<Section> downSection = findDownSection(other);

        downSection.ifPresent(it -> validateDistance(it, other));
    }

    private Optional<Section> findDownSection(final Section other) {
        return value.stream().filter(it -> it.getDownStation().equals(other.getDownStation())).findAny();
    }

    private Optional<Section> findUpSection(final Section other) {
        return value.stream().filter(it -> it.getUpStation().equals(other.getUpStation())).findAny();
    }

    private void validateDistance(final Section section, final Section other) {
        if (other.isGreaterOrEqualTo(section)) {
            throw new DistanceTooLongException();
        }
    }

    public List<Section> getSections() {
        return value;
    }

    @Override
    public String toString() {
        return "Sections{" + "value=" + value + '}';
    }
}
