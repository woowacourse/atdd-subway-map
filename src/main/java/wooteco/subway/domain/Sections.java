package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections {

    public static final String NOTING_UP_STATION_ERROR_MESSAGE = "구간에 등록된 출발역이 없습니다.";
    public static final String NOTING_DOWN_STATION_ERROR_MESSAGE = "구간에 등록된 도착역이 없습니다.";
    private static final String EXIST_STATION_ERROR_MESSAGE = "기존에 존재하는 구간입니다.";
    private static final String NO_CREATE_RANGE_SECTION_ERROR_MESSAGE = "생성할 수 없는 구간입니다.";
    private static final String NOT_FOUND_NEXT_STATION_ERROR_MESSAGE = "다음 역을 찾을 수 없습니다.";
    private static final String NOT_FOUNT_START_STATION_ERROR_MESSAGE = "시작 구간을 찾을 수 없습니다.";

    private final List<Section> values;

    public Sections(List<Section> sections) {
        values = sort(new ArrayList<>(sections));
    }

    public boolean existUpStation(Station station) {
        return values.stream()
                .anyMatch(value -> value.hasSameUpStation(station));
    }

    public boolean existDownStation(Station station) {
        return values.stream()
                .anyMatch(value -> value.hasSameDownStation(station));
    }

    public Section findContainsUpStation(Station station) {
        return values.stream()
                .filter(value -> value.hasSameUpStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOTING_UP_STATION_ERROR_MESSAGE));
    }

    public Section findContainsDownStation(Station station) {
        return values.stream()
                .filter(value -> value.hasSameDownStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOTING_DOWN_STATION_ERROR_MESSAGE));
    }

    private Station findNext(List<Section> sections, List<Section> values, Station next) {
        for (Section section : sections) {
            if (section.hasSameUpStation(next)) {
                values.add(section);
                return section.getDownStation();
            }
        }
        throw new IllegalArgumentException(NOT_FOUND_NEXT_STATION_ERROR_MESSAGE);
    }

    public List<Section> splitSection(Station upStation, Station downStation, Section target) {
        if (existUpStation(upStation)) {
            Section section = findContainsUpStation(upStation);
            return section.splitFromUpStation(target);
        }

        if (existDownStation(downStation)) {
            Section section = findContainsDownStation(downStation);
            return section.splitFromDownStation(target);
        }
        return List.of(target);
    }

    public List<Section> margeSection(Station target, Line line) {
        if (existUpStation(target) && !existDownStation(target)) {
            return List.of(findContainsUpStation(target));
        }
        if (existDownStation(target) && !existUpStation(target)) {
            return List.of(findContainsDownStation(target));
        }

        return marge(target, line);
    }

    public void validateHasSameSection(Station upStation, Station downStation) {
        if (existUpStation(upStation) && existDownStation(downStation)) {
            throw new IllegalArgumentException(EXIST_STATION_ERROR_MESSAGE);
        }
    }

    public List<Station> getStations() {
        Set<Station> stations = new LinkedHashSet<>();
        for (Section value : values) {
            stations.add(value.getUpStation());
            stations.add(value.getDownStation());
        }
        return List.copyOf(stations);
    }

    public List<Section> getValues() {
        return List.copyOf(values);
    }

    public void validateContainsStation(Station upStation, Station downStation) {
        if (!hasStationFrontOrBack(upStation, downStation)
                && !existUpStation(upStation) && !existDownStation(downStation)) {
            throw new IllegalArgumentException(NO_CREATE_RANGE_SECTION_ERROR_MESSAGE);
        }
    }


    public boolean hasStationFrontOrBack(Station upStation, Station downStation) {
        return existUpStation(downStation) || existDownStation(upStation);
    }

    private List<Section> sort(List<Section> sections) {
        List<Section> values = new ArrayList<>();
        Station next = findFirstStation(sections);
        while (values.size() != sections.size()) {
            next = findNext(sections, values, next);
        }
        return values;
    }

    private Station findFirstStation(List<Section> sections) {
        List<Station> upStations = createUpStations(sections);
        List<Station> downStations = createDownStations(sections);

        return upStations.stream()
                .filter(upStation -> !downStations.contains(upStation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUNT_START_STATION_ERROR_MESSAGE));
    }

    private List<Section> marge(Station target, Line line) {
        Section upSection = findContainsDownStation(target);
        Section downSection = findContainsUpStation(target);
        Section resultSection = new Section(line, upSection.getUpStation(), downSection.getDownStation(),
                upSection.getDistance() + downSection.getDistance());
        return List.of(resultSection, upSection, downSection);
    }

    private List<Station> createUpStations(List<Section> sections) {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> createDownStations(List<Section> sections) {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }
}
