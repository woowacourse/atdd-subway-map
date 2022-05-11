package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Sections implements Iterable<Section> {
    private static final int LIMIT_LOW_SIZE = 1;
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        validateSize(sections);
        this.sections = new ArrayList<>(sections);
    }

    private void validateSize(List<Section> sections) {
        if (sections.size() < LIMIT_LOW_SIZE) {
            throw new IllegalArgumentException("구간은 최소 한 개가 있어야 합니다.");
        }
    }

    public void checkCanAddAndUpdate(Section section) {
        validateCanConnect(section);
        validateAlreadyConnected(section);
        validateSectionCanDividedWithSameUpStation(section);
        validateSectionCanDividedWithSameDownStation(section);
        sections.add(section);
    }

    private void validateCanConnect(Section section) {
        boolean canConnect = sections.stream()
            .anyMatch(section::canConnect);

        if (!canConnect) {
            throw new IllegalArgumentException("해당 구간은 연결 지점이 없습니다");
        }
    }

    private void validateAlreadyConnected(Section section) {
        boolean isExistUpStation = sections.stream()
            .anyMatch(section::isSameUpStation);
        boolean isExistDownStation = sections.stream()
            .anyMatch(section::isSameDownStation);

        if (isExistUpStation && isExistDownStation) {
            throw new IllegalArgumentException("해당 구간은 이미 이동 가능합니다.");
        }
    }

    private void validateSectionCanDividedWithSameUpStation(Section section) {
        Section sectionWithSameUpStation = sections.stream()
            .filter(section1 -> section1.isSameUpStation(section))
            .findFirst()
            .orElse(null);

        if (sectionWithSameUpStation == null) {
            return;
        }

        if (!sectionWithSameUpStation.canInsert(section)) {
            throw new IllegalArgumentException("해당 구간은 추가될 수 없습니다.");
        }

        sectionWithSameUpStation.changeDownStationAndDistance(section);
    }

    private void validateSectionCanDividedWithSameDownStation(Section section) {
        Section sectionWithSameDownStation = sections.stream()
            .filter(section1 -> section1.isSameDownStation(section))
            .findFirst()
            .orElse(null);

        if (sectionWithSameDownStation == null) {
            return;
        }

        if (!sectionWithSameDownStation.canInsert(section)) {
            throw new IllegalArgumentException("해당 구간은 추가될 수 없습니다.");
        }

        sectionWithSameDownStation.changeUpStationAndDistance(section);
    }

    public int size() {
        return sections.size();
    }

    public Section findById(long id) {
        return sections.stream()
            .filter(section -> section.isSameId(id))
            .findFirst()
            .orElse(null);
    }

    public List<Station> getStations() {
        List<Station> upStations = sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        List<Station> downStations = sections.stream().map(Section::getDownStation).collect(Collectors.toList());

        Set<Station> stations = new HashSet<>();
        stations.addAll(upStations);
        stations.addAll(downStations);

        return new ArrayList<>(stations);
    }

    public Section deleteAndUpdate(Station station) {
        long count = sections.stream()
            .filter(section -> section.isSameDownStation(station) || section.isSameUpStation(station))
            .count();

        if (count == 0) {
            throw new IllegalArgumentException("해당 역을 지나지 않습니다.");
        }

        if (count == 1) {
            Section deletedSection = sections.stream()
                .filter(section -> section.isSameDownStation(station) || section.isSameUpStation(station))
                .findFirst().orElseThrow();
            sections.remove(deletedSection);
            return deletedSection;
        }

        Section sectionWithSameUpStation = sections.stream()
            .filter(section -> section.isSameUpStation(station))
            .findFirst().orElseThrow();

        Section sectionWithSameDownStation = sections.stream()
            .filter(section -> section.isSameDownStation(station))
            .findFirst().orElseThrow();

        sectionWithSameDownStation.connect(sectionWithSameUpStation);
        sections.remove(sectionWithSameUpStation);
        return sectionWithSameUpStation;
    }

    private class SectionIterator implements Iterator<Section> {

        private int current = 0;

        @Override
        public boolean hasNext() {
            return current < sections.size();
        }

        @Override
        public Section next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return sections.get(current++);
        }
    }

    @Override
    public Iterator<Section> iterator() {
        return new SectionIterator();
    }

    @Override
    public void forEach(Consumer<? super Section> action) {
        Iterable.super.forEach(action);
    }
}
