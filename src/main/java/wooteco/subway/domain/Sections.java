package wooteco.subway.domain;

import java.util.List;
import java.util.Optional;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void validateDistance(int distance) {
        int totalDistance = sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
        if (totalDistance != 0 && totalDistance <= distance) {
            throw new IllegalArgumentException("기존 구간의 거리보다 크거나 같은 구간은 추가할 수 없습니다.");
        }
    }

    public void validateStations(Station upStation, Station downStation) {
        checkBothExist(upStation, downStation);
        checkBothDoNotExist(upStation, downStation);
    }

    private void checkBothExist(Station upStation, Station downStation) {
        int count = (int)sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .filter(section -> section.getDownStation().equals(downStation))
                .count();

        if (count == 1) {
            throw new IllegalArgumentException("이미 존재하는 구간입니다.");
        }
    }

    private void checkBothDoNotExist(Station upStation, Station downStation) {
        int upStationCount = (int)sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .count();

        int downStationCount = (int)sections.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .count();

        if (upStationCount == 0 && downStationCount == 0) {
            throw new IllegalArgumentException("상행역과 하행역 모두 존재하지 않습니다.");
        }
    }

    private Optional<Section> findSameUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst();
    }

    private Optional<Section> findSameDownStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findFirst();
    }

    public void updateSection(Station upStation, Station downStation, int distance) {
        Optional<Section> sameUpStation = findSameUpStation(upStation);
        if (sameUpStation.isPresent()) {
            Section section = sameUpStation.get();
            List<Section> splitSections = section.splitSectionIfSameUpStation(downStation, distance);
            update(section, splitSections);
            return;
        }

        Optional<Section> sameDownStation = findSameDownStation(downStation);
        if (sameDownStation.isPresent()) {
            Section section = sameDownStation.get();
            List<Section> splitSections = section.splitSectionIfSameDownStation(upStation, distance);
            update(section, splitSections);
            return;
        }

        sections.add(new Section(getLineId(), upStation, downStation, distance));
    }

    private void update(Section section, List<Section> splitSections) {
        sections.add(splitSections.get(0));
        sections.add(splitSections.get(1));
        sections.remove(section);
    }

    public void deleteSection(Station station) {
        if (sections.size() == 1) {
            throw new IllegalArgumentException("마지막 구간은 삭제할 수 없습니다.");
        }

        Optional<Section> sameUpStation = findSameUpStation(station);
        Optional<Section> sameDownStation = findSameDownStation(station);
        if (sameUpStation.isPresent() && sameDownStation.isPresent()) {
            Section upSection = sameDownStation.get();
            Section downSection = sameUpStation.get();
            Section concatSection = upSection.concatSections(downSection);

            delete(upSection, downSection, concatSection);
            return;
        }

        throw new IllegalArgumentException("해당 역이 포함된 구간이 존재하지 않습니다.");
    }

    private void delete(Section upSection, Section downSection, Section concatSection) {
        sections.remove(upSection);
        sections.remove(downSection);
        sections.add(concatSection);
    }

    public long getLineId() {
        return sections.get(0).getLineId();
    }

    public boolean isPresent() {
        return !sections.isEmpty();
    }

    public List<Section> getSections() {
        return sections;
    }
}
