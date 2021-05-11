package wooteco.subway.section.domain;

import wooteco.subway.exception.IllegalSectionStatusException;
import wooteco.subway.exception.SectionUpdateException;

import java.util.ArrayList;
import java.util.List;
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
        validateAddSection(section);
        return null;
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

        Section targetSection = sections.stream()
                .filter(existSection ->
                        (existSection.isUpStationId(upStationId) || existSection.isDownStationId(downStationId) ||
                                (existSection.isUpStationId(downStationId) || existSection.isDownStationId(upStationId))))
                .findFirst().orElseThrow(IllegalSectionStatusException::new);

        if (targetSection.isUpStationId(upStationId) || targetSection.isDownStationId(downStationId)) {
            validateDistance(targetSection, section);
        }

        return targetSection;
    }

    private void validateDistance(Section targetSection, Section section) {
        if (targetSection.compareDistance(section.getDistance())) {
            throw new SectionUpdateException("추가할 구간의 거리는 기존 구간 거리보다 작아야 합니다.");
        }
    }
}
