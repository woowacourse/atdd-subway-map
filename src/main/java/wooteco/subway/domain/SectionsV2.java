package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SectionsV2 {

    private final List<SectionV2> values;

    public SectionsV2(List<SectionV2> values) {
        this.values = new ArrayList<>(values);
    }

    public void add(SectionV2 section) {
        validateStationDuplicateSection(section);
        validateOneStationExistSection(section);
        validateStationNotExistSection(section);
        validateUpdateMiddleSection(section);
        values.add(section);
    }

    private void validateStationDuplicateSection(SectionV2 section) {
        final boolean isExist = values.stream()
                .anyMatch(value -> value.isSameStation(section));

        if (isExist) {
            throw new IllegalArgumentException("이미 존재하는 동일한 구간입니다.");
        }
    }

    private void validateOneStationExistSection(SectionV2 section) {
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();

        if (isSectionConnect(upStation, downStation) || isSectionConnect(downStation, upStation)) {
            throw new IllegalArgumentException("이미 연결되어 있는 구간입니다.");
        }
    }

    private boolean isSectionConnect(Station upStation, Station downStation) {
        return getUpStation().contains(upStation) && getDownStation().contains(downStation);
    }

    private List<Station> getUpStation() {
        return values.stream()
                .map(SectionV2::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> getDownStation() {
        return values.stream()
                .map(SectionV2::getDownStation)
                .collect(Collectors.toList());
    }

    public Optional<SectionV2> findUpdate(List<SectionV2> sections) {
        return values.stream()
                .filter(value -> findUpdateSection(sections, value))
                .findFirst();
    }

    private boolean findUpdateSection(List<SectionV2> sections, SectionV2 section) {
        return sections.stream()
                .anyMatch(value -> value.isUpdate(section));
    }

    private void validateStationNotExistSection(SectionV2 section) {
        final boolean isExist = values.stream()
                .anyMatch(value -> value.hasStation(section));

        if (!isExist) {
            throw new IllegalArgumentException("구간에 존재하지 않는 역입니다.");
        }
    }

    private void validateUpdateMiddleSection(SectionV2 section) {
        values.stream()
                .filter(value -> value.isSameUpStation(section.getUpStation())
                        || value.isSameDownStation(section.getDownStation()))
                .findAny()
                .ifPresent(updateSection -> updateMiddleSection(section, updateSection));
    }

    private void updateMiddleSection(SectionV2 section, SectionV2 updateSection) {
        validateSectionDistance(section, updateSection);
        updateMiddleUpOrDownStation(section, updateSection);
    }

    private void updateMiddleUpOrDownStation(SectionV2 section, SectionV2 updateSection) {
        if (updateSection.isSameUpStation(section.getUpStation())) {
            updateStationAndDistance(
                    updateSection, section.getDownStation(), updateSection.getDownStation(), section.getDistance());
            return;
        }
        updateStationAndDistance(
                updateSection, updateSection.getUpStation(), section.getUpStation(), section.getDistance());
    }

    private void updateStationAndDistance(SectionV2 updateSection, Station upStation, Station downStation, int distance) {
        updateSection.updateStation(upStation, downStation);
        updateSection.splitDistance(distance);
    }

    private void validateSectionDistance(SectionV2 section, SectionV2 updateSection) {
        if (updateSection.isOverDistance(section)) {
            throw new IllegalArgumentException("기존의 구간보다 더 긴 구간은 추가할 수 없습니다.");
        }
    }

    public List<SectionV2> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "SectionsV2{" +
                "values=" + values +
                '}';
    }
}
