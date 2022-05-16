package wooteco.subway.domain;

import wooteco.subway.dto.SectionsToBeCreatedAndUpdated;
import wooteco.subway.dto.SectionsToBeDeletedAndUpdated;
import wooteco.subway.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

public class Sections {

    private final List<Section> values;

    public Sections(List<Section> values) {
        this.values = sortSections(values);
    }

    private List<Section> sortSections(List<Section> sections) {
        List<Section> sortedSections = new ArrayList<>();
        Section section = findLastUpSection(sections);
        sortedSections.add(section);

        while (sections.size() > sortedSections.size()) {
            Long nextUpStationId = section.getDownStationId();
            section = findNextSection(sections, nextUpStationId);
            sortedSections.add(section);
        }
        return sortedSections;
    }

    private Section findLastUpSection(List<Section> sections) {
        return sections.stream()
                .filter(s -> isLastUpStation(sections, s))
                .findAny()
                .orElseThrow(() -> new NotFoundException("상행 종점을 찾지 못했습니다."));
    }

    private boolean isLastUpStation(List<Section> sections, Section section) {
        return sections.stream()
                .noneMatch(s -> s.getDownStationId().equals(section.getUpStationId()));
    }

    private Section findNextSection(List<Section> sections, Long nextUpStationId) {
        return sections.stream()
                .filter(s -> s.getUpStationId().equals(nextUpStationId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("구간들 정렬중 다음 구간을 찾지 못하였습니다."));
    }

    public SectionsToBeCreatedAndUpdated add(Section newSection) {
        validateExistStationInLine(newSection);
        Long currentLastUpStationId = values.get(0).getUpStationId();
        Long currentLastDownStationId = values.get(values.size() - 1).getDownStationId();

        if (newSection.isNewLastStation(currentLastUpStationId, currentLastDownStationId)) {
            return new SectionsToBeCreatedAndUpdated(newSection);
        }
        return addMiddleSection(newSection);
    }

    private void validateExistStationInLine(Section section) {
        boolean hasUpStation = hasStation(section.getUpStationId());
        boolean hasDownStation = hasStation(section.getDownStationId());
        if (!hasUpStation && !hasDownStation) {
            throw new IllegalArgumentException("구간을 추가하기 위해서는 노선에 들어있는 역이 필요합니다.");
        }
        if (hasUpStation && hasDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
    }

    private boolean hasStation(Long stationId) {
        return values.stream()
                .anyMatch(s -> s.getUpStationId().equals(stationId) || s.getDownStationId().equals(stationId));
    }

    private SectionsToBeCreatedAndUpdated addMiddleSection(Section newSection) {
        Section existNearSection = findNearSection(newSection);
        validateNewSectionDistance(newSection, existNearSection);
        Section sectionThatNeedToBeUpdated = null;
        if (newSection.getUpStationId().equals(existNearSection.getUpStationId())) {
            sectionThatNeedToBeUpdated = new Section(existNearSection.getId(), existNearSection.getLineId(),
                    newSection.getDownStationId(), existNearSection.getDownStationId(),
                    existNearSection.getDistance() - newSection.getDistance());
        }
        if (newSection.getDownStationId().equals(existNearSection.getDownStationId())) {
            sectionThatNeedToBeUpdated = new Section(existNearSection.getId(), existNearSection.getLineId(),
                    existNearSection.getUpStationId(), newSection.getUpStationId(),
                    existNearSection.getDistance() - newSection.getDistance());
        }
        return new SectionsToBeCreatedAndUpdated(newSection, sectionThatNeedToBeUpdated);
    }

    private Section findNearSection(Section newSection) {
        return values.stream()
                .filter(s -> s.getUpStationId().equals(newSection.getUpStationId()) ||
                        s.getDownStationId().equals(newSection.getDownStationId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("중간역 생성중 기존역을 찾지 못하였습니다."));
    }

    private void validateNewSectionDistance(Section newSection, Section existNearSection) {
        if (newSection.getDistance() >= existNearSection.getDistance()) {
            throw new IllegalArgumentException("새로운 구간의 길이는 기존 역 사이의 길이보다 작아야 합니다.");
        }
    }

    public SectionsToBeDeletedAndUpdated delete(Long stationId) {
        validateExistStation(stationId);
        validateRemainOneSection();
        Section currentLastUpSection = values.get(0);
        Section currentLastDownSection = values.get(values.size() - 1);
        if (currentLastUpSection.isUpStation(stationId) ||
                currentLastDownSection.isDownStation(stationId)) {
            return deleteLastSection(currentLastUpSection, currentLastDownSection, stationId);
        }

        return deleteMiddleSection(stationId);
    }

    private void validateExistStation(Long stationId) {
        if (!hasStation(stationId)) {
            throw new NotFoundException("현재 라인에 존재하지 않는 역입니다.");
        }
    }

    private void validateRemainOneSection() {
        if (values.size() == 1) {
            throw new IllegalArgumentException("구간이 하나인 노선에서는 구간 삭제가 불가합니다.");
        }
    }

    private SectionsToBeDeletedAndUpdated deleteLastSection(Section lastUpSection, Section lastDownSection, Long stationId) {
        if (stationId.equals(lastUpSection.getUpStationId())) {
            return new SectionsToBeDeletedAndUpdated(lastUpSection);
        }
        if (stationId.equals(lastDownSection.getDownStationId())) {
            return new SectionsToBeDeletedAndUpdated(lastDownSection);
        }
        return null;
    }

    private SectionsToBeDeletedAndUpdated deleteMiddleSection(Long stationId) {
        Section upSideStation = extractUpSideStation(stationId);
        Section downSideStation = extractDownSideStation(stationId);
        Section sectionToBeUpdated = new Section(upSideStation.getId(), upSideStation.getLineId(), upSideStation.getUpStationId(),
                downSideStation.getDownStationId(), upSideStation.getDistance() + downSideStation.getDistance());
        return new SectionsToBeDeletedAndUpdated(downSideStation, sectionToBeUpdated);
    }

    private Section extractUpSideStation(Long stationId) {
        return values.stream()
                .filter(s -> s.getDownStationId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("중간역 삭제중 상행역을 찾지 못하였습니다."));
    }

    private Section extractDownSideStation(Long stationId) {
        return values.stream()
                .filter(s -> s.getUpStationId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("중간역 삭제중 하행역을 찾지 못하였습니다."));
    }

    public List<Long> getSortedStationIds() {
        List<Long> sortedStationIds = new ArrayList<>();
        sortedStationIds.add(values.get(0).getUpStationId());
        for (Section value : values) {
            sortedStationIds.add(value.getDownStationId());
        }
        return sortedStationIds;
    }
}
