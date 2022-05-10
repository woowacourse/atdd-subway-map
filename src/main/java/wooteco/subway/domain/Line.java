package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Station upStation;
    private Station downStation;
    private Long distance;
    List<Section> sections = new ArrayList<>();

    public Line(Long id, String name, String color, Station upStation, Station downStation, Long distance,
                List<Section> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.sections = sections;
    }

    public Line(Long id, String name, String color, Station upStation, Station downStation, Long distance) {
        this(id, name, color, upStation, downStation, distance, new ArrayList<>());
    }

    public Line(String name, String color, Station upStation, Station downStation, Long distance) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void addSection(Section section) {
        validateCanAddSection(section);
        boolean upStationExist = containsStation(section.getUpStation());
        boolean downStationExist = containsStation(section.getUpStation());
        // 상행 종점 등록 || 상행 구간 사이에 들어가기
        if (!upStationExist && downStationExist) {
            // TODO: 갈래길 방지 및 거리 validate
            sections.add(0, section);
        }

        // 하행 종점 등록 || 하행 구간 사이에 들어가기
        if (upStationExist && !downStationExist) {
            // TODO: 갈래길 방지 및 거리 valdiate
            sections.add(section);
        }
    }

    private void validateCanAddSection(Section section) {
        boolean upStationExist = containsStation(section.getUpStation());
        boolean downStationExist = containsStation(section.getUpStation());
        if (!upStationExist && !downStationExist) {
            throw new IllegalArgumentException(
                    String.format("%s와 %s 모두 존재하지 않아 구간을 등록할 수 없습니다.", section.getUpStationName(),
                            section.getDownStationName()));
        }
        if (upStationExist && downStationExist) {
            throw new IllegalArgumentException(
                    String.format("%s와 %s 이미 모두 등록 되어있어 구간을 등록할 수 없습니다.", section.getUpStationName(),
                            section.getDownStationName()));
        }
    }

    private boolean containsStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.contains(station));
    }

    public Station calculateUpStation() {
        List<Station> upStations = getUpperStations();
        List<Station> downStations = getDownerStations();

        return upStations.stream()
                .filter(station -> !downStations.contains(station))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("상행역이 존재하지 않습니다."));
    }

    private Station calculateDownStation() {
        List<Station> upStations = getUpperStations();
        List<Station> downStations = getDownerStations();

        return downStations.stream()
                .filter(station -> !upStations.contains(station))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("하행역이 존재하지 않습니다."));
    }

    private List<Station> getDownerStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private List<Station> getUpperStations() {
        return sections.stream()
                    .map(Section::getUpStation)
                    .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(getId(), line.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "Line{" +
                "upStation=" + upStation.getName() +
                ", downStation=" + downStation.getName() +
                '}';
    }
}
