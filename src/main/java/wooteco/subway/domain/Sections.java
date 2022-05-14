package wooteco.subway.domain;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

public class Sections {

    private final List<Section> value;

    public Sections(List<Section> value) {
        this.value = new ArrayList<>(value);
    }

    public void append(Section section) {
        validateSameSection(section);
        validateContainsStation(section);
        validateNotContainsStation(section);

        if (isUpTerminus(section) || isDownTerminus(section)) {
            value.add(section);
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
        return section.getDownStation().equals(getUpStation())
                && !existsStation(section.getUpStation());
    }

    private boolean isDownTerminus(Section section) {
        return section.getUpStation().equals(getDownStation())
                && !existsStation(section.getDownStation());
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
        value.add(section);
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
        value.add(section);
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
            value.remove(findByUpStation(getUpStation()));
            return;
        }

        if (getDownStation().equals(station)) {
            value.remove(findByDownStation(getDownStation()));
            return;
        }

        removeMiddleStation(station);
    }

    private Station getUpStation() {
        List<Station> upStations = getUpStations();
        List<Station> downStations = getDownStations();

        upStations.removeAll(downStations);
        return upStations.get(0);
    }

    private Station getDownStation() {
        List<Station> upStations = getUpStations();
        List<Station> downStations = getDownStations();

        downStations.removeAll(upStations);
        return downStations.get(0);
    }

    private List<Station> getUpStations() {
        return value.stream()
                .map(Section::getUpStation)
                .collect(toList());
    }

    private List<Station> getDownStations() {
        return value.stream()
                .map(Section::getDownStation)
                .collect(toList());
    }

    private void removeMiddleStation(Station station) {
        Section frontSection = findByDownStation(station);
        Section backSection = findByUpStation(station);

        Line line = frontSection.getLine();
        Station upStation = frontSection.getUpStation();
        Station downStation = backSection.getDownStation();
        int distance = frontSection.getDistance() + backSection.getDistance();

        value.remove(frontSection);
        value.remove(backSection);

        value.add(new Section(line, upStation, downStation, distance));
    }

    public List<Station> getSortedStations() {
        List<Section> value = getSortedValue();
        List<Station> stations = value.stream()
                .map(Section::getUpStation)
                .collect(toList());

        Section lastSection = value.get(value.size() - 1);
        stations.add(lastSection.getDownStation());

        return stations;
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

    public List<Section> getSortedValue() {
        List<Section> sections = new ArrayList<>();
        Section firstSection = findByUpStation(getUpStation());

        addSection(sections, firstSection);

        return sections;
    }

    private void addSection(List<Section> sections, Section prevSection) {
        sections.add(prevSection);

        for (Section currentSection : value) {
            checkConnect(sections, prevSection, currentSection);
        }
    }

    private void checkConnect(List<Section> sections, Section prevSection, Section currentSection) {
        if (prevSection.isConnect(currentSection)) {
            addSection(sections, currentSection);
        }
    }
}
