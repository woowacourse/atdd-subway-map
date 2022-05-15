package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {

    private static final int SECTIONS_MINIMUM_SIZE = 1;

    private final List<Section> values;

    public Sections(List<Section> values) {
        this.values = new ArrayList<>(values);
    }

    public void add(Section section) {
        validateStationDuplicateSection(section);
        validateOneStationExistSection(section);
        validateStationNotExistSection(section);
        validateUpdateMiddleSection(section);
        values.add(section);
    }

    private void validateStationDuplicateSection(Section section) {
        final boolean isExist = values.stream()
                .anyMatch(value -> value.isSameStation(section));

        if (isExist) {
            throw new IllegalArgumentException("이미 존재하는 동일한 구간입니다.");
        }
    }

    private void validateOneStationExistSection(Section section) {
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();

        if (isSectionConnect(upStation, downStation) || isSectionConnect(downStation, upStation)) {
            throw new IllegalArgumentException("이미 연결되어 있는 구간입니다.");
        }
    }

    private boolean isSectionConnect(Station upStation, Station downStation) {
        return getUpStations().contains(upStation) && getDownStations().contains(downStation);
    }

    private List<Station> getUpStations() {
        return values.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> getDownStations() {
        return values.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    public Optional<Section> findUpdate(List<Section> sections) {
        return values.stream()
                .filter(value -> findUpdateSection(sections, value))
                .findFirst();
    }

    private boolean findUpdateSection(List<Section> sections, Section section) {
        return sections.stream()
                .anyMatch(value -> value.isUpdate(section));
    }

    private void validateStationNotExistSection(Section section) {
        final boolean isExist = values.stream()
                .anyMatch(value -> value.hasStation(section));

        if (!isExist) {
            throw new IllegalArgumentException("구간에 존재하지 않는 역입니다.");
        }
    }

    private void validateUpdateMiddleSection(Section section) {
        values.stream()
                .filter(value -> value.isSameUpStation(section.getUpStation())
                        || value.isSameDownStation(section.getDownStation()))
                .findAny()
                .ifPresent(updateSection -> updateMiddleSection(section, updateSection));
    }

    private void updateMiddleSection(Section section, Section updateSection) {
        validateSectionDistance(section, updateSection);
        updateMiddleUpOrDownStation(section, updateSection);
    }

    private void updateMiddleUpOrDownStation(Section section, Section updateSection) {
        if (updateSection.isSameUpStation(section.getUpStation())) {
            updateStationAndDistance(
                    updateSection, section.getDownStation(), updateSection.getDownStation(), section.getDistance());
            return;
        }
        updateStationAndDistance(
                updateSection, updateSection.getUpStation(), section.getUpStation(), section.getDistance());
    }

    private void updateStationAndDistance(Section updateSection, Station upStation, Station downStation, int distance) {
        updateSection.updateStation(upStation, downStation);
        updateSection.splitDistance(distance);
    }

    private void validateSectionDistance(Section section, Section updateSection) {
        if (updateSection.isOverDistance(section)) {
            throw new IllegalArgumentException("기존의 구간보다 더 긴 구간은 추가할 수 없습니다.");
        }
    }

    public List<Section> delete(Station station) {
        validateDeleteSize(values);
        List<Section> sections = values.stream()
                .filter(value -> value.isSameUpStation(station)
                        || value.isSameDownStation(station))
                .collect(Collectors.toList());
        values.removeAll(sections);
        return sections;
    }

    private void validateDeleteSize(List<Section> values) {
        if (values.size() <= SECTIONS_MINIMUM_SIZE) {
            throw new IllegalArgumentException("구간이 1개만 등록되어 있을 경우에는 삭제할 수 없습니다.");
        }
    }

    public Station findFirstStation() {
        List<Station> downStations = getDownStations();
        List<Station> upStations = getUpStations();

        return upStations.stream()
                .filter(station -> !downStations.contains(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("첫번째 구간을 찾을 수 없습니다."));
    }

    public Optional<Station> nextStation(Station station) {
        return values.stream()
                .filter(value -> value.isSameUpStation(station))
                .map(Section::getDownStation)
                .findFirst();
    }

    public List<Section> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "Sections{" +
                "values=" + values +
                '}';
    }
}
