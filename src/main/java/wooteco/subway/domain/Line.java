package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Station upStation;
    private Station downStation;
    private Long distance;
    private Sections sections;

    public Line(Long id, String name, String color, Station upStation, Station downStation, Long distance,
                Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.sections = sections;
    }

    public Line(Long id, String name, String color, Station upStation, Station downStation, Long distance) {
        this(id, name, color, upStation, downStation, distance, new Sections(
                Collections.singletonList(new Section(id, upStation, downStation, distance))));
    }

    public Line(String name, String color, Station upStation, Station downStation, Long distance) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.sections = new Sections(
                Collections.singletonList(new Section(id, upStation, downStation, distance)));
    }

    public List<Section> addSection(Section section) {
        validateCanAddSection(section);
        boolean upStationExist = sections.containsStation(section.getUpStation());
        boolean downStationExist = sections.containsStation(section.getDownStation());
        // 상행 종점 등록 || 상행 구간 사이에 들어가기
        if (!upStationExist && downStationExist) {
            Station lineUpEndStation = sections.calculateUpStation();
            // 상행 종점 등록
            if (section.getDownStation().equals(lineUpEndStation)) {
                sections.add(section);
                return List.of(section);
            }
            // 기존에 노선에 있던 역을 하행 삼아 그 역 사이에 들어가기
            Section sectionWithLowerStation = sections.findSectionWithLowerStation(section.getDownStation());
            List<Section> newAddedSections = sectionWithLowerStation.putBetweenDownStation(section);
            Section removedSection = sections.changeSectionWithNewSections(sectionWithLowerStation, newAddedSections);
            return extractResultList(newAddedSections, removedSection);
        }

        // 하행 종점 등록 || 하행 구간 사이에 들어가기
        if (upStationExist && !downStationExist) {
            Station lineDownEndStation = sections.calculateDownStation();
            // 하행 종점 등록
            if (section.getUpStation().equals(lineDownEndStation)) {
                sections.add(section);
                return List.of(section);
            }

            // 기존에 노선에 있던 역을 상행 삼아 그 역 사이에 들어가기
            Section sectionWithUpperStation = sections.findSectionWithUpperStation(section.getUpStation());
            List<Section> newAddedSections = sectionWithUpperStation.putBetweenUpStation(section);
            Section removedSection = sections.changeSectionWithNewSections(sectionWithUpperStation, newAddedSections);
            return extractResultList(newAddedSections, removedSection);
        }
        throw new IllegalArgumentException("구간을 추가하지 못하는 예외가 발생하였습니다.");
    }

    private List<Section> extractResultList(List<Section> newAddedSections, Section removedSection) {
        List<Section> resultList = new ArrayList<>();
        resultList.addAll(newAddedSections);
        resultList.add(removedSection);
        return resultList;
    }

    private void validateCanAddSection(Section section) {
        boolean upStationExist = sections.containsStation(section.getUpStation());
        boolean downStationExist = sections.containsStation(section.getDownStation());
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

    private void changeEndStations() {
        if (sections.size() >= 1) {
            this.upStation = sections.calculateUpStation();
            this.downStation = sections.calculateDownStation();
        }
    }

    public List<Section> removeStation(Station station) {
        validateCanRemoveStation(station);
        // 상행 종점인 경우
        Station upStation = getUpStation();
        Station downStation = getDownStation();
        if (station.equals(upStation)) {
            Section sectionWithUpperStation = sections.findSectionWithUpperStation(station);
            sections.remove(sectionWithUpperStation);
            return List.of(sectionWithUpperStation);
        }
        // 하행 종점인 경우
        if (station.equals(downStation)) {
            Section sectionWithLowerStation = sections.findSectionWithLowerStation(station);
            sections.remove(sectionWithLowerStation);
            return List.of(sectionWithLowerStation);
        }
        // 중간에 낀 경우 제거하려는 역을 상행으로 잡고 제거한다.
        Section sectionWithUpperStation = sections.findSectionWithUpperStation(station);
        Section sectionWithLowerStation = sections.findSectionWithLowerStation(station);
        sections.remove(sectionWithUpperStation);
        sections.remove(sectionWithLowerStation);
        Section mergedSection = new Section(sectionWithLowerStation.getLineId(), sectionWithUpperStation.getUpStation(),
                sectionWithLowerStation.getDownStation(), sectionWithUpperStation.getDistance() + sectionWithLowerStation
                .getDistance());

        return List.of(mergedSection, sectionWithUpperStation, sectionWithLowerStation);
    }

    public ArrayList<Station> getUniqueStations() {
        return sections.getStations();
    }

    private void validateCanRemoveStation(Station station) {
        if (sections.size() <= 1) {
            throw new IllegalStateException("구간이 하나인 노선에서 마지막 구간을 제거할 수 없습니다.");
        }

        if (!sections.containsStation(station)) {
            throw new IllegalArgumentException("존재하지 않는 역을 제거할 수 없습니다");
        }
    }

    public boolean contains(Station station) {
        return sections.containsStation(station);
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
        changeEndStations();
        return upStation;
    }

    public Station getDownStation() {
        changeEndStations();
        return downStation;
    }

    public Long getDistance() {
        return distance;
    }

    public Sections getSections() {
        return sections;
    }

    @Override
    public String toString() {
        return "Line{" +
                "upStation=" + upStation.getName() +
                ", downStation=" + downStation.getName() +
                '}';
    }
}
