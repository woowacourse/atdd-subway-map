package wooteco.subway.section.domain;

import wooteco.subway.exception.IllegalSectionStatusException;

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

        throw new IllegalSectionStatusException("잘못된 구간 정보입니다.");
    }

    private List<Section> sorting(List<Section> sections, Section topSection) {
        List<Section> sortedSections = new ArrayList<>();

        sortedSections.add(topSection);

        int size = sections.size();
        Long curDownStationId = topSection.getDownStationId();

        // todo 인덴트 2
        for (int i = 0; i < size - 1; i++) {
            for(Section section : sections) {
                if(section.isUpStationId(curDownStationId)) {
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
}
