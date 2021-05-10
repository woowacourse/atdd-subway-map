package wooteco.subway.section;

import wooteco.subway.exception.InvalidAddSectionException;

import java.util.List;
import java.util.function.BiPredicate;

import static java.util.stream.Collectors.toList;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section newSection) {
        validate(newSection);
        sections.add(newSection);
    }

    private void validate(Section newSection) {
        validateConnected(newSection, this::isConnected);
        validateConnected(newSection, this::isNotExisted);
        validateDistance(newSection);
    }

    private void validateConnected(Section newSection, BiPredicate<Section, Section> biPredicate) {
        sections.stream()
            .filter(section -> biPredicate.test(section, newSection))
            .findAny()
            .orElseThrow(InvalidAddSectionException::new);
    }

    private boolean isConnected(Section newSection, Section section) {
        return isEndpoint(newSection, section) || isIntermediate(newSection, section);
    }

    private boolean isEndpoint(Section newSection, Section section) {
        return section.isUpStation(newSection.getDownStationId()) ||
                section.isDownStation(newSection.getUpStationId());
    }

    private boolean isNotExisted(Section newSection, Section section) {
        return !(section.isUpStation(newSection.getUpStationId()) &&
            section.isDownStation(newSection.getDownStationId()));
    }

    private void validateDistance(Section newSection) {
        sections.stream()
            .filter(section -> section.isUpStation(newSection.getUpStationId()))
            .findAny()
            .ifPresent(section -> isValidDistance(newSection, section));
    }

    private void isValidDistance(Section newSection, Section section) {
        if (section.isSameOrLongDistance(newSection)) {
            throw new InvalidAddSectionException();
        }
    }

    private boolean isIntermediate(Section newSection, Section section) {
        return section.isUpStation(newSection.getUpStationId()) ||
            section.isDownStation(newSection.getDownStationId());
    }

    public boolean isBiggerThanOne() {
        return sections.size() > 1;
    }

    public boolean isOne() {
        return sections.size() == 1;
    }

    public Section merge(Long stationId) {
        int newDistance = sections.stream()
            .mapToInt(Section::getDistance)
            .sum();

        Section upSection = getSection(stationId, this::matchDownStation);
        Section downSection = getSection(stationId, this::matchUpStation);

        return new Section(upSection.getUpStationId(), downSection.getDownStationId(), newDistance);
    }

    private Section getSection(Long stationId, BiPredicate<Long, Section> biPredicate) {
        return sections.stream()
            .filter(section -> biPredicate.test(stationId, section))
            .findAny()
            .get();
    }

    private boolean matchUpStation(Long stationId, Section section) {
        return section.isUpStation(stationId);
    }

    private boolean matchDownStation(Long stationId, Section section) {
        return section.isDownStation(stationId);
    }

    public List<Long> sectionIds() {
        return sections.stream()
            .map(Section::getId)
            .collect(toList());
    }
}
