package wooteco.subway.section.domain;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        validatePossibleToAdd(section);
        if (isAddToEndPoint(section)) {
            sections.add(section);
            return;
        }
        addToBetween(section);
    }

    private void addToBetween(Section newSection) {
        Section section = findSectionToConnect(newSection);
        section.validateAddableDistance(newSection);
        sections.remove(section);
        addUpStation(section, newSection);
        addDownStation(section, newSection);
    }


    private void addUpStation(Section section, Section newSection) {
        if (section.isSameDownStationId(newSection)) {
            sections.add(new Section(newSection.getLineId(), newSection.getUpStationId(),
                    section.getDownStationId(), newSection.getDistance()));
            sections.add(new Section(newSection.getLineId(), section.getUpStationId(),
                    newSection.getDownStationId(), section.getDistance().minus(newSection.getDistance())));
        }
    }

    private void addDownStation(Section section, Section newSection) {
        if (section.isSameUpStationId(newSection)) {
            sections.add(new Section(newSection.getLineId(), section.getUpStationId(),
                    newSection.getDownStationId(), newSection.getDistance()));
            sections.add(new Section(newSection.getLineId(), newSection.getUpStationId(),
                    section.getDownStationId(), section.getDistance().minus(newSection.getDistance())));
        }
    }

    private Section findSectionToConnect(Section newSection) {
        return sections.stream()
                .filter(section -> section.hasUpStationIdOrDownStationId(newSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("연결할 역을 찾지 못했습니다."));
    }

    private boolean isAddToEndPoint(Section newSection) {
        boolean existUpStationId = sections.stream()
                .anyMatch(section -> section.isSameUpStationId(newSection));
        boolean existDownStationId = sections.stream()
                .anyMatch(section -> section.isSameDownStationId(newSection));
        return !existUpStationId && !existDownStationId;
    }

    private void validatePossibleToAdd(Section newSection) {
        List<Long> stationsIds = this.stationIds();
        boolean existUpStation = isExistStationId(stationsIds, newSection.getUpStationId());
        boolean existDownStation = isExistStationId(stationsIds, newSection.getDownStationId());
        validateAlreadyExistSectionOfStation(existUpStation, existDownStation);
        validateNotExistSectionOfStation(existUpStation, existDownStation);
    }

    private boolean isExistStationId(List<Long> stationsIds, Long upStationId) {
        return stationsIds.contains(upStationId);
    }

    private void validateNotExistSectionOfStation(boolean existUpStation, boolean existDownStation) {
        if (!existUpStation && !existDownStation) {
            throw new IllegalArgumentException("연결할 수 있는 역이 구간내에 없습니다.");
        }
    }
    private void validateAlreadyExistSectionOfStation(boolean existUpStation, boolean existDownStation) {
        if (existUpStation && existDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 존재합니다.");
        }
    }

    private List<Long> stationIds() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStationId(), section.getDownStationId()))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Section> toList() {
        return sections;
    }
}
