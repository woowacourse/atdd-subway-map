package wooteco.subway.domain;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Sections {

    private final LinkedList<Section> value;

    public Sections(List<Section> value) {
        this.value = new LinkedList<>(value);
    }

    public void append(Section section) {
        validateSameSection(section);
        validateContainsStation(section);
        validateNotContainsStation(section);

        if (isUpTerminus(section)) {
            value.addFirst(section);
            return;
        }

        if (isDownTerminus(section)) {
            value.addLast(section);
            return;
        }

        decideForkedLoad(section);
    }

    private void validateSameSection(Section section) {
        if (value.stream().anyMatch(section::isSameUpAndDownStation)) {
            throw new IllegalArgumentException("구간이 노선에 이미 등록되어 추가할 수 없습니다.");
        }
    }

    private void validateContainsStation(Section section) {
        if (existsStation(section.getUpStation()) && existsStation(section.getDownStation())) {
            throw new IllegalArgumentException("상행역과 하행역 모두 노선에 포함되어 있으므로 추가할 수 없습니다.");
        }
    }

    private void validateNotContainsStation(Section section) {
        if (!existsStation(section.getUpStation()) && !existsStation(section.getDownStation())) {
            throw new IllegalArgumentException("상행역과 하행역이 모두 노선에 포함되지 않으므로 추가할 수 없습니다.");
        }
    }

    private boolean isUpTerminus(Section section) {
        return section.getDownStation().equals(getUpStation()) && !existsStation(section.getUpStation());
    }

    private boolean isDownTerminus(Section section) {
        return section.getUpStation().equals(getDownStation()) && !existsStation(section.getDownStation());
    }

    private void decideForkedLoad(Section section) {
        if (isFrontForkedLoad(section)) {
            changeFrontSection(section);
            return;
        }

        if (isBackForkedLoad(section)) {
            changeBackSection(section);
            return;
        }

        throw new IllegalArgumentException("새로운 구간의 길이가 기존 구간의 길이 보다 크거나 같으므로 추가할 수 없습니다.");
    }

    private boolean isFrontForkedLoad(Section section) {
        if (existsStation(section.getUpStation())) {
            Section frontSection = findByUpStation(section.getUpStation());
            return frontSection.getDistance() > section.getDistance();
        }

        return false;
    }

    private void changeFrontSection(Section section) {
        Section frontSection = findByUpStation(section.getUpStation());
        frontSection.changeUpStation(section);
        int frontIndex = value.indexOf(frontSection);
        value.remove(frontSection);
        value.add(frontIndex, section);
        value.add(frontIndex + 1, frontSection);
    }

    private boolean isBackForkedLoad(Section section) {
        if (existsStation(section.getDownStation())) {
            Section backSection = findByDownStation(section.getDownStation());
            return backSection.getDistance() > section.getDistance();
        }

        return false;
    }

    private void changeBackSection(Section section) {
        Section backSection = findByDownStation(section.getDownStation());
        backSection.changeDownStation(section);
        int backIndex = value.indexOf(backSection);
        value.add(backIndex + 1, section);
    }

    private boolean existsStation(Station station) {
        return value.stream()
                .anyMatch(section -> section.existsStation(station));
    }

    public void remove(Station station) {
        if (value.size() == 1) {
            throw new IllegalArgumentException("구간이 1개만 존재하므로 삭제가 불가능 합니다.");
        }

        if (getUpStation().equals(station)) {
            value.remove(value.get(0));
            return;
        }

        if (getDownStation().equals(station)) {
            value.remove(value.get(value.size() - 1));
            return;
        }

        removeMiddleStation(station);
    }

    private Station getUpStation() {
        Section frontSection = value.get(0);
        return frontSection.getUpStation();
    }

    private Station getDownStation() {
        Section backSection = value.get(value.size() - 1);
        return backSection.getDownStation();
    }

    private void removeMiddleStation(Station station) {
        Section frontSection = findByDownStation(station);
        Section backSection = findByUpStation(station);

        int frontIndex = value.indexOf(frontSection);

        Line line = frontSection.getLine();
        Station upStation = frontSection.getUpStation();
        Station downStation = backSection.getDownStation();
        int distance = frontSection.getDistance() + backSection.getDistance();

        value.remove(frontSection);
        value.remove(backSection);

        value.add(frontIndex, new Section(line, upStation, downStation, distance));
    }

    private Section findByUpStation(Station station) {
        return value.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 구간이 존재하지 않습니다."));
    }

    private Section findByDownStation(Station station) {
        return value.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 구간이 존재하지 않습니다."));
    }

    public List<Station> getStations() {
        List<Station> stations = value.stream()
                .map(Section::getUpStation)
                .collect(toList());

        Section lastSection = value.get(value.size() - 1);
        stations.add(lastSection.getDownStation());

        return stations;
    }

    public List<Section> getValue() {
        return Collections.unmodifiableList(value);
    }
}
