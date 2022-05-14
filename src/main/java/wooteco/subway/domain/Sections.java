package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private static final int MINIMUM_SECTIONS_COUNT = 1;

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
        validateSectionCount();
    }

    public void add(final Section section) {
        validateSection(section);

        Section existSection = getExistSection(section);
        if (existSection.isAddingEndSection(section)) {
            sections.add(section);
            return;
        }
        addStationInSection(existSection, section);
    }

    public void delete(final Long stationId) {
        validateDeletedSectionCount();

        if (isEndStation(stationId)) {
            Section existSection = getExistSection(stationId);
            sections.remove(existSection);
            return;
        }
        deleteStationInSection(stationId);
    }

    private void addStationInSection(final Section existSection, final Section section) {
        Section replacedSection = Section.replaced(existSection, section);

        sections.remove(existSection);
        sections.add(section);
        sections.add(replacedSection);
    }

    private void deleteStationInSection(final Long stationId) {
        Section sectionIncludedUpStation = getExistUpStation(stationId);
        Section sectionIncludedDownStation = getExistDownStation(stationId);
        Section deletedSection = Section.deleted(sectionIncludedDownStation, sectionIncludedUpStation);

        sections.remove(sectionIncludedUpStation);
        sections.remove(sectionIncludedDownStation);
        sections.add(deletedSection);
    }

    private void validateSection(final Section section) {
        List<Long> stationIds = getStationIdsInSection();
        if (hasAllStation(section, stationIds)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 지하철 노선에 존재합니다.");
        }
        if (hasNotAnyStation(section, stationIds)) {
            throw new IllegalArgumentException("추가하려는 구간이 노선에 포함되어 있지 않습니다.");
        }
    }

    private void validateSectionCount() {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("노선에 구간이 하나 이상 존재해야 합니다.");
        }
    }

    private void validateDeletedSectionCount() {
        if (sections.size() <= MINIMUM_SECTIONS_COUNT) {
            throw new IllegalArgumentException("노선에 구간이 2개 이상이어야 삭제 가능합니다.");
        }
    }

    private boolean hasAllStation(final Section section, final List<Long> stationIds) {
        return stationIds.contains(section.getUpStationId()) && stationIds.contains(section.getDownStationId());
    }

    private boolean hasNotAnyStation(final Section section, final List<Long> stationIds) {
        return !stationIds.contains(section.getUpStationId()) && !stationIds.contains(section.getDownStationId());
    }

    private boolean isEndStation(final Long stationId) {
        return (getUpStationIds().contains(stationId) && !getDownStationIds().contains(stationId))
                || (!getUpStationIds().contains(stationId) && getDownStationIds().contains(stationId));
    }

    private List<Long> getStationIdsInSection() {
        List<Long> allStationIds = new ArrayList<>();
        allStationIds.addAll(getUpStationIds());
        allStationIds.addAll(getDownStationIds());

        return allStationIds.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Long> getUpStationIds() {
        return sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
    }

    private List<Long> getDownStationIds() {
        return sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());
    }

    private Section getExistSection(final Long sectionId) {
        return sections.stream()
                .filter(exist -> exist.existStation(sectionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("구간 정보를 찾을 수 없습니다."));
    }

    private Section getExistSection(final Section section) {
        return sections.stream()
                .filter(exist -> exist.existStation(section.getUpStationId())
                        || exist.existStation(section.getDownStationId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("구간 정보를 찾을 수 없습니다."));
    }

    private Section getExistUpStation(final Long sectionId) {
        return sections.stream()
                .filter(section -> section.hasUpStation(sectionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("구간 내 지하철역 정보를 찾을 수 없습니다."));
    }

    private Section getExistDownStation(final Long sectionId) {
        return sections.stream()
                .filter(section -> section.hasDownStation(sectionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("구간 내 지하철역 정보를 찾을 수 없습니다."));
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
