package wooteco.subway.domain;

import static wooteco.subway.domain.dto.AddSectionResult.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import wooteco.subway.domain.dto.AddSectionResult;
import wooteco.subway.domain.dto.RemoveStationResult;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Station upStation;
    private Station downStation;
    private Long distance;
    private Sections sections;

    public Line(String name, String color, Station upStation, Station downStation, Long distance) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.sections = new Sections(new Section(id, upStation, downStation, distance));
    }

    public Line(Long id, String name, String color, Station upStation, Station downStation, Long distance,
                Sections sections) {
        this(name, color, upStation, downStation, distance);
        this.id = id;
        this.sections = sections;
    }

    public Line(Long id, String name, String color, Station upStation, Station downStation, Long distance) {
        this(name, color, upStation, downStation, distance);
        this.id = id;
        this.sections = new Sections(Collections.singletonList(new Section(id, upStation, downStation, distance)));
    }

    public AddSectionResult addSection(Section section) {
        validateCanAddSection(section);

        if (onlyDownStationExistInLine(section)) {
            if (isNewSectionUpEnd(section, sections)) {
                sections.add(section);
                return createWithNewEndSection(section);
            }
            return insertStationWithExistDownStation(section);
        }

        if (onlyUpStationExistInLine(section)) {
            if (isNewSectionDownEnd(section, sections)) {
                sections.add(section);
                return createWithNewEndSection(section);
            }
            return insertStationWithExistUpStation(section);
        }
        throw new IllegalArgumentException("구간을 추가하지 못하는 예외가 발생하였습니다.");
    }

    private AddSectionResult insertStationWithExistUpStation(Section section) {
        Section sectionWithUpperStation = sections.findSectionWithUpperStation(section.getUpStation());
        List<Section> newAddedSections = sectionWithUpperStation.putBetweenUpStation(section);
        Section removedSection = sections.changeSectionWithNewSections(sectionWithUpperStation, newAddedSections);
        return createSplitSections(newAddedSections, removedSection);
    }

    private AddSectionResult insertStationWithExistDownStation(Section section) {
        Section sectionWithLowerStation = sections.findSectionWithLowerStation(section.getDownStation());
        List<Section> newAddedSections = sectionWithLowerStation.putBetweenDownStation(section);
        Section removedSection = sections.changeSectionWithNewSections(sectionWithLowerStation, newAddedSections);
        return createSplitSections(newAddedSections, removedSection);
    }

    private boolean isNewSectionDownEnd(Section section, Sections sections) {
        Station existDownEndStation = sections.calculateDownStation();
        return section.getUpStation().equals(existDownEndStation);
    }

    private boolean isNewSectionUpEnd(Section section, Sections sections) {
        Station existUpEndStation = sections.calculateUpStation();
        return section.getDownStation().equals(existUpEndStation);
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

    public RemoveStationResult removeStation(Station station) {
        validateCanRemoveStation(station);
        if (isTargetStationUpEndStation(station)) {
            Section sectionWithUpperStation = sections.findSectionWithUpperStation(station);
            sections.remove(sectionWithUpperStation);
            return RemoveStationResult.createWithRemovedUpEndSection(sectionWithUpperStation);
        }

        if (isTargetStationDownEndStation(station)) {
            Section sectionWithLowerStation = sections.findSectionWithLowerStation(station);
            sections.remove(sectionWithLowerStation);
            return RemoveStationResult.createWithRemovedDownEndSection(sectionWithLowerStation);
        }

        return removeStationAndMerge(station);
    }

    private RemoveStationResult removeStationAndMerge(Station station) {
        Section sectionWithUpperStation = sections.findSectionWithUpperStation(station);
        Section sectionWithLowerStation = sections.findSectionWithLowerStation(station);
        sections.remove(sectionWithUpperStation);
        sections.remove(sectionWithLowerStation);

        Section mergedSection = new Section(sectionWithLowerStation.getLineId(), sectionWithUpperStation.getUpStation(),
                sectionWithLowerStation.getDownStation(),
                sectionWithUpperStation.getDistance() + sectionWithLowerStation.getDistance());

        return RemoveStationResult.createWithMergedAndRemovedSections(mergedSection, sectionWithUpperStation, sectionWithLowerStation);
    }

    private boolean isTargetStationDownEndStation(Station station) {
        return station.equals(getDownStation());
    }

    private boolean isTargetStationUpEndStation(Station station) {
        return station.equals(getUpStation());
    }

    public List<Station> getSortedStations() {
        List<Station> result = new ArrayList<>();
        List<Station> stations = sections.getStations();
        Station startUpEndStation = sections.calculateUpStation();
        Section currentSection = sections.findSectionWithUpperStation(startUpEndStation);
        result.add(startUpEndStation);

        while (result.size() < stations.size() - 1) {
            result.add(currentSection.getDownStation());
            currentSection = sections.findSectionWithUpperStation(currentSection.getDownStation());
        }
        result.add(currentSection.getDownStation());
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

    private boolean onlyDownStationExistInLine(Section section) {
        boolean upStationExist = sections.containsStation(section.getUpStation());
        boolean downStationExist = sections.containsStation(section.getDownStation());
        return !upStationExist && downStationExist;
    }

    private boolean onlyUpStationExistInLine(Section section) {
        boolean upStationExist = sections.containsStation(section.getUpStation());
        boolean downStationExist = sections.containsStation(section.getDownStation());
        return upStationExist && !downStationExist;
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
