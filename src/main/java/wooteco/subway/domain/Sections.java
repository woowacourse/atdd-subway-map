package wooteco.subway.domain;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> values;

    public Sections(List<Section> values) {
        this.values = values;
    }

    public boolean checkAddSectionInUpStation(Station upStation, int distance) {
        Optional<Section> optionalSection = values.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst();
        return checkDistance(distance, optionalSection);
    }

    public Section getOriginUpStationSection(Long stationId) {
        return values.stream()
                .filter(section -> section.getUpStation().getId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알맞은 상행 역을 찾을 수 없습니다."));
    }

    public boolean checkAddSectionInDownStation(Station downStation, int distance) {
        Optional<Section> optionalSection = values.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findFirst();
        return checkDistance(distance, optionalSection);
    }

    public Section getOriginDownStationSection(Long stationId) {
        return values.stream()
                .filter(section -> section.getDownStation().getId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알맞은 하행 역을 찾을 수 없습니다."));
    }

    public boolean checkSameStations(Station upStation, Station downStation) {
        for (Section section : values) {
            if (section.isSameStations(upStation.getId(), downStation.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean canAddEndOfTheLine(Station upStation, Station downStation) {
        return values.stream()
                .anyMatch(section ->
                        section.getUpStation().equals(downStation) || section.getDownStation().equals(upStation));
    }

    private boolean checkDistance(int distance, Optional<Section> optionalSection) {
        if (optionalSection.isEmpty()) {
            return false;
        }
        Section section = optionalSection.get();
        if (section.isBetweenDistance(distance)) {
            return true;
        }
        throw new IllegalArgumentException(
                String.format("거리가 같거나 먼 가지 구간은 등록할 수 없습니다. 기존: %d, 요청: %d", section.getDistance(), distance));
    }

    public boolean isZeroSize() {
        return values.size() == 0;
    }

    public boolean isUpAndDownStation() {
        return values.size() == 2;
    }

    public List<Long> getSectionIds() {
        return values.stream()
                .map(Section::getId)
                .collect(Collectors.toList());
    }

    public int getSumDistance() {
        return values.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }
}
