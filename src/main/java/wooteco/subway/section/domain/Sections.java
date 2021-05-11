package wooteco.subway.section.domain;

import wooteco.subway.exception.IllegalSectionStatusException;
import wooteco.subway.exception.SectionUpdateException;
import wooteco.subway.exception.notfoundexception.NotFoundSectionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sortSections(sections);
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Section> sortSections(List<Section> sections) {
        Section topSection = findTopSection(sections);
        return sorting(sections, topSection);
    }

    private Section findTopSection(List<Section> sections) {
        List<Long> downStationIds = sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());

        // todo 인덴트 2
        for (Section section : sections) {
            if (downStationIds.stream()
                    .noneMatch(section::isUpStationId)) {
                return section;
            }
        }

        throw new IllegalSectionStatusException();
    }

    private List<Section> sorting(List<Section> sections, Section topSection) {
        List<Section> sortedSections = new ArrayList<>();

        sortedSections.add(topSection);

        int size = sections.size();
        Long curDownStationId = topSection.getDownStationId();

        // todo 인덴트 2
        for (int i = 0; i < size - 1; i++) {
            for (Section section : sections) {
                if (section.isUpStationId(curDownStationId)) {
                    sortedSections.add(section);
                    curDownStationId = section.getDownStationId();
                    break;
                }
            }
        }

        return sortedSections;
    }

    public List<Long> getStationsId() {
        List<Long> stationsId = sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());

        stationsId.add(getLastSection().getDownStationId());

        return stationsId;
    }

    private Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    public Section addSection(Section section) {
        Section targetSection = validateAddSection(section);

        if (isBetweenAddCase(section, targetSection)) {
            return getUpdateSection(section, targetSection);
        }
        return section;
    }

    private Section getUpdateSection(Section section, Section targetSection) {
        int updateSectionDistance = targetSection.getDistance() - section.getDistance();
        if (section.isUpStationId(targetSection.getUpStationId())) {
            return new Section(targetSection.getId(), targetSection.getLineId(), section.getDownStationId(), targetSection.getDownStationId(), updateSectionDistance);
        }
        return new Section(targetSection.getId(), targetSection.getLineId(), targetSection.getUpStationId(), section.getUpStationId(), updateSectionDistance);
    }

    private Section validateAddSection(Section section) {
        Long upStationId = section.getUpStationId();
        Long downStationId = section.getDownStationId();

        if (sections.stream()
                .anyMatch(existSection ->
                        (existSection.isUpStationId(upStationId) && existSection.isDownStationId(downStationId)) ||
                                (existSection.isUpStationId(downStationId) && existSection.isDownStationId(upStationId)))) {
            throw new SectionUpdateException("중복된 구간입니다.");
        }

        if (sections.stream()
                .noneMatch(existSection ->
                        (existSection.isUpStationId(upStationId) || existSection.isDownStationId(downStationId)) ||
                                (existSection.isUpStationId(downStationId) || existSection.isDownStationId(upStationId)))) {
            throw new SectionUpdateException("상행역 또는 하행역이 포함되어야 합니다.");
        }

        Section targetSection = targetSection(upStationId, downStationId);

        if (isBetweenAddCase(section, targetSection)) {
            validateDistance(targetSection, section);
        }

        return targetSection;
    }

    private Section targetSection(Long upStationId, Long downStationId) {
        if (isEndStation(upStationId) || isEndStation(downStationId)) {
            return isTargetSectionExistEnd(upStationId, downStationId);
        }

        return isTargetSectionExistBetween(upStationId, downStationId);
    }

    private Section isTargetSectionExistBetween(Long upStationId, Long downStationId) {
        List<Section> sections = this.sections.stream()
                .filter(existSection ->
                        (existSection.isUpStationId(upStationId) || existSection.isDownStationId(downStationId) ||
                                (existSection.isUpStationId(downStationId) || existSection.isDownStationId(upStationId))))
                .collect(Collectors.toList());

        Long fixedId = findFixedId(sections, upStationId, downStationId);
        if(fixedId.equals(upStationId)) {
            return sections.stream()
                    .filter(section -> section.isUpStationId(fixedId))
                    .findFirst()
                    .orElseThrow(NotFoundSectionException::new);
        }
        return sections.stream()
                .filter(section -> section.isDownStationId(fixedId))
                .findFirst()
                .orElseThrow(NotFoundSectionException::new);
    }

    private Section isTargetSectionExistEnd(Long upStationId, Long downStationId) {
        return sections.stream()
                .filter(existSection ->
                        (existSection.isUpStationId(upStationId) || existSection.isDownStationId(downStationId) ||
                                (existSection.isUpStationId(downStationId) || existSection.isDownStationId(upStationId))))
                .findFirst().orElseThrow(IllegalSectionStatusException::new);
    }

    private Long findFixedId(List<Section> sections, Long upStationId, Long downStationId) {
        Section firstSection = sections.get(0);
        if (firstSection.isUpStationId(upStationId) || firstSection.isDownStationId(upStationId)) {
            return upStationId;
        }
        return downStationId;
    }

    private boolean isBetweenAddCase(Section section, Section targetSection) {
        return targetSection.isUpStationId(section.getUpStationId()) || targetSection.isDownStationId(section.getDownStationId());
    }

    private void validateDistance(Section targetSection, Section section) {
        if (targetSection.compareDistance(section.getDistance())) {
            throw new SectionUpdateException("추가할 구간의 거리는 기존 구간 거리보다 작아야 합니다.");
        }
    }

    public Optional<Section> deleteSection(Long lineId, Long stationId) {
        validateDeleteSection();
        if (isEndStation(stationId)) {
            return Optional.empty();
        }

        Section upSection = sections.stream()
                .filter(section -> section.isDownStationId(stationId))
                .findFirst()
                .orElseThrow(NotFoundSectionException::new);
        Section downSection = sections.stream()
                .filter(section -> section.isUpStationId(stationId))
                .findFirst()
                .orElseThrow(NotFoundSectionException::new);
        int updateSectionDistance = upSection.getDistance() + downSection.getDistance();

        return Optional.of(new Section(lineId, upSection.getUpStationId(), downSection.getDownStationId(), updateSectionDistance));
    }

    // todo
    private void validateDeleteSection() {
    }

    private boolean isEndStation(Long stationId) {
        Section topSection = sections.get(0);
        Section bottomSection = sections.get(sections.size() - 1);

        return topSection.isUpStationId(stationId) || bottomSection.isDownStationId(stationId);
    }
}
