package wooteco.subway.domain.section;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections = new ArrayList<>();

    public Sections(List<Section> unOrderedSections) {
        Long upwardTerminalStationId = findUpwardTerminalId(unOrderedSections);
        sortSections(unOrderedSections, upwardTerminalStationId);
    }

    private Long findUpwardTerminalId(List<Section> unOrderedSections) {
        List<Long> upStationIds = unOrderedSections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());

        List<Long> downStationIds = unOrderedSections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());

        upStationIds.removeAll(downStationIds);
        return upStationIds.get(0);
    }

    private void sortSections(List<Section> unOrderedSections, Long stationId) {
        Section section;
        Long nextStationId = stationId;
        do {
            section = findSection(unOrderedSections, nextStationId);
            sections.add(section);
            nextStationId = section.getDownStationId();
        } while (hasNextDownwardSection(unOrderedSections, nextStationId));
    }

    private Section findSection(List<Section> unOrderedSections, Long terminalId) {
        return unOrderedSections.stream()
                .filter(section -> section.hasUpwardStation(terminalId))
                .findFirst()
                .get();
    }

    private boolean hasNextDownwardSection(List<Section> unOrderedSections, Long downStationId) {
        return unOrderedSections.stream()
                .anyMatch(section -> section.hasUpwardStation(downStationId));
    }

    public void addSection(Section section) {
        validateIfAlreadyExistsInLine(section);
        validateIfBothStationNotExistsInLine(section);
    }

    private void validateIfAlreadyExistsInLine(Section section) {
        if (isBothStationExistsInLine(section)) {
            throw new SubwayException(HttpStatus.BAD_REQUEST, "두 역이 이미 노선에 등록되어 있습니다.");
        }
    }

    private boolean isBothStationExistsInLine(Section section) {
        return isStationExists(section.getUpStationId()) && isStationExists(section.getDownStationId());
    }

    private boolean isStationExists(Long stationId) {
        boolean upwardExistence = sections.stream()
                .anyMatch(section -> section.hasUpwardStation(stationId));
        boolean downwardExistence = sections.stream()
                .anyMatch(section -> section.hasDownwardStation(stationId));

        return upwardExistence || downwardExistence;
    }

    private void validateIfBothStationNotExistsInLine(Section section) {
        if (!isStationExists(section.getUpStationId()) && !isStationExists(section.getDownStationId())) {
            throw new SubwayException(HttpStatus.BAD_REQUEST, "노선에 역들이 존재하지 않습니다.");
        }
    }

    public void validateIfPossibleToDelete() {
        if (this.sections.size() == 1) {
            throw new SubwayException(HttpStatus.BAD_REQUEST, "구간이 하나뿐이므로 삭제 불가능합니다.");
        }
    }
}
