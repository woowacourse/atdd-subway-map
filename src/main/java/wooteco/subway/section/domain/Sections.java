package wooteco.subway.section.domain;

import wooteco.subway.line.exception.LineException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections){
        this.sections = sections;
    }

    private Optional<Section> downStation(final Long search){
         return sections.stream()
                .filter(section -> section.isFrontStationId(search))
                .findFirst();
    }

    public void validateToAdd(final Section section) {
        // TODO :: 둘 다 이미 존재하거나, 둘 다 없는 경우
    }

    public Section findSectionInclude(final Section section) {
        return sections.stream()
                .filter(sectionInLine -> sectionInLine.isSameBackStation(section)
                        || sectionInLine.isSameFrontStation(section))
                .findFirst()
                .orElseThrow(()-> new LineException("해당 구간을 찾을 수 없습니다."));
    }

    public List<Section> sectionsIncludeStation(final Long stationId) {
        return sections.stream()
                .filter(section -> section.isIncludeStation(stationId))
                .collect(Collectors.toList());
    }
}
