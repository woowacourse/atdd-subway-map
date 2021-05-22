package wooteco.subway.line.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import wooteco.subway.exception.IllegalUserInputException;
import wooteco.subway.exception.NotExistItemException;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void sort() {
        Map<Long, Section> upStationToSection = getUpStationToSection();
        Long upStationId = getStationId(upStationToSection);
        sections.clear();
        while (upStationToSection.containsKey(upStationId)) {
            Section section = upStationToSection.get(upStationId);
            sections.add(section);
            upStationId = section.getDownStationId();
        }
    }

    private Map<Long, Section> getUpStationToSection() {
        Map<Long, Section> upStationToSection = new HashMap<>();
        for (Section section : sections) {
            upStationToSection.put(section.getUpStationId(), section);
        }
        return upStationToSection;
    }

    private Long getStationId(Map<Long, Section> upStationToSection) {
        Map<Long, Integer> stationIdCount = new HashMap<>();

        for (Section section : sections) {
            Long upStationId = section.getUpStationId();
            Long downStationId = section.getDownStationId();
            stationIdCount.put(upStationId, stationIdCount.getOrDefault(upStationId, 0) + 1);
            stationIdCount.put(downStationId, stationIdCount.getOrDefault(downStationId, 0) + 1);
        }

        return stationIdCount.keySet().stream()
            .filter(key -> stationIdCount.get(key) == 1)
            .filter(upStationToSection::containsKey)
            .findFirst()
            .orElseThrow(NotExistItemException::new);
    }

    public Section findJoinResultSection(Section addSection) {
        validateJoinSection(addSection);
        Section sideSection = findJoinSideSection(addSection);
        if (Objects.nonNull(sideSection)) {
            return sideSection;
        }

        return findJoinMiddleSection(addSection);
    }

    private void validateJoinSection(Section section) {
        Set<Long> stationIds = getStationIds();
        boolean isDuplicateUpStation = isDuplicateStation(section.getUpStationId(), stationIds);
        boolean isDuplicateDownStation = isDuplicateStation(section.getDownStationId(), stationIds);
        if (isAddSection(isDuplicateUpStation, isDuplicateDownStation)) {
            throw new IllegalUserInputException();
        }
    }

    private boolean isAddSection(boolean isDuplicateUpStation, boolean isDuplicateDownStation) {
        return isDuplicateDownStation && isDuplicateUpStation;
    }

    private Set<Long> getStationIds() {
        Set<Long> stationIds = new HashSet<>();
        for (Section section : sections) {
            stationIds.add(section.getDownStationId());
            stationIds.add(section.getUpStationId());
        }
        return stationIds;
    }

    private boolean isDuplicateStation(Long stationId, Set<Long> stationIds) {
        return stationIds.contains(stationId);
    }

    private Section findJoinSideSection(Section addSection) {
        Section firstSection = sections.get(0);
        Section lastSection = sections.get(sections.size() - 1);
        if (addSection.isDownEqualUp(firstSection)) {
            return firstSection;
        }
        if (addSection.isUpEqualDown(lastSection)) {
            return lastSection;
        }
        return null;
    }

    private Section findJoinMiddleSection(Section addSection) {
        Optional<Section> upEqualStation = sections.stream()
            .filter(addSection::isUpEqualUp)
            .findFirst();

        if (upEqualStation.isPresent()) {
            Section section = upEqualStation.get();
            validateDistance(upEqualStation.get(), addSection);
            return new Section(section.getId(), addSection.getDownStationId(),
                section.getDownStationId(), section.getDiffDistance(addSection));
        }

        Optional<Section> downEqualStation = sections.stream()
            .filter(addSection::isDownEqualDown)
            .findFirst();

        if (downEqualStation.isPresent()) {
            Section section = downEqualStation.get();
            validateDistance(downEqualStation.get(), addSection);
            return new Section(section.getId(), section.getUpStationId(),
                addSection.getUpStationId(), section.getDiffDistance(addSection));
        }
        throw new IllegalUserInputException();
    }

    private void validateDistance(Section section, Section addSection) {
        if (section.getDiffDistance(addSection) <= 0) {
            throw new IllegalUserInputException();
        }
    }

    public Section findDeleteResultSection(Long deleteStationId) {
        validateDeleteSize();
        Section lastSection = sections.get(sections.size() - 1);

        if (lastSection.getDownStationId().equals(deleteStationId)) {
            return lastSection;
        }

        return sections.stream()
            .filter(section -> section.getUpStationId().equals(deleteStationId))
            .findFirst()
            .orElseThrow(IllegalUserInputException::new);
    }

    private void validateDeleteSize() {
        if (sections.size() <= 1) {
            throw new IllegalUserInputException();
        }
    }

    public Section findDeleteUpdateResultSection(Long stationId, Section deleteSection) {
        Section firstSection = sections.get(0);
        Section lastSection = sections.get(sections.size() - 1);

        if (firstSection.getUpStationId().equals(stationId)
            || lastSection.getDownStationId().equals(stationId)) {
            return null;
        }

        int index = sections.indexOf(deleteSection);
        Section section = sections.get(index - 1);

        return new Section(
            section.getId(),
            section.getUpStationId(),
            deleteSection.getDownStationId(),
            section.getDistance() + deleteSection.getDistance());
    }

    public List<Section> getSections() {
        return new ArrayList<>(sections);
    }
}
