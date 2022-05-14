package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import wooteco.subway.exception.IllegalInputException;
import wooteco.subway.exception.section.NoSuchSectionException;

public class SectionsDomain {

    private static final int MIN_SECTION_SIZE = 1;
    private static final int MERGE_REQUIRED_SIZE = 2;

    private final List<SectionDomain> value;

    public SectionsDomain(final List<SectionDomain> value) {
        this.value = value;
    }

    public SectionsDomain findDeletableSections(final Station stationToDelete) {
        validateMinSize();
        final List<SectionDomain> deletableSections = value
                .stream()
                .filter(it -> it.contains(stationToDelete))
                .collect(Collectors.toList());
        if (deletableSections.isEmpty()) {
            throw new NoSuchSectionException();
        }
        return new SectionsDomain(deletableSections);
    }

    private void validateMinSize() {
        if (value.size() == MIN_SECTION_SIZE) {
            throw new IllegalInputException("구간을 삭제할 수 없습니다.");
        }
    }

    public boolean needMerge() {
        return value.size() == MERGE_REQUIRED_SIZE;
    }

    public SectionDomain toMergedSection() {
        final SectionDomain first = value.get(0);
        final SectionDomain second = value.get(1);
        return first.merge(second);
    }

    public List<Station> toStation() {
        Station upStation = findEndUpStation();

        final List<Station> stations = new ArrayList<>();
        stations.add(upStation);
        while (stations.size() != value.size() + 1) {
            final SectionDomain section = findSectionByUpStation(upStation);
            upStation = section.getDownStation();
            stations.add(upStation);
        }
        return stations;
    }

    private Station findEndUpStation() {
        if (value.size() < MIN_SECTION_SIZE) {
            throw new NoSuchSectionException();
        }
        final List<Station> upStations = value.stream()
                .map(SectionDomain::getUpStation)
                .collect(Collectors.toList());
        final List<Station> downStations = value.stream()
                .map(SectionDomain::getDownStation)
                .collect(Collectors.toList());
        upStations.removeAll(downStations);
        return upStations.get(0);
    }

    private SectionDomain findSectionByUpStation(final Station upStation) {
        return value
                .stream()
                .filter(it -> it.hasSameUpStation(upStation))
                .findFirst()
                .orElseThrow(NoSuchSectionException::new);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SectionsDomain that = (SectionsDomain) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "SectionsDomain{" +
                "value=" + value +
                '}';
    }
}
