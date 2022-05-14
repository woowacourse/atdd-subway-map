package wooteco.subway.domain;

import wooteco.subway.dto.SectionsToBeDeletedAndUpdated;
import wooteco.subway.dto.SectionsToBeCreatedAndUpdated;
import wooteco.subway.exception.AccessNoneDataException;
import wooteco.subway.exception.SectionServiceException;

import java.util.List;

public class Sections {

    private final List<Section> values;

    public Sections(List<Section> values) {
        this.values = values;
    }

    public SectionsToBeCreatedAndUpdated add(Section newSection) {
        validateExistStationInLine(newSection);
        Long currentLastUpStationId = findLastUpSection().getUpStationId();
        Long currentLastDownStationId = findLastDownSection().getDownStationId();

        if (newSection.isNewLastStation(currentLastUpStationId, currentLastDownStationId)) {
            return new SectionsToBeCreatedAndUpdated(newSection);
        }
        return addMiddleSection(newSection);
    }

    private SectionsToBeCreatedAndUpdated addMiddleSection(Section newSection) {
        Section existNearSection = findNearSection(newSection);
        if (newSection.getDistance() >= existNearSection.getDistance()) {
            throw new SectionServiceException("새로운 구간의 길이는 기존 역 사이의 길이보다 작아야 합니다.");
        }
        Section sectionThatNeedToBeUpdated = new Section(existNearSection.getId(), existNearSection.getLineId(),
                newSection.getUpStationId(), existNearSection.getDownStationId(),
                existNearSection.getDistance() - newSection.getDistance());

        return new SectionsToBeCreatedAndUpdated(newSection, sectionThatNeedToBeUpdated);
    }

    private void validateExistStationInLine(Section section) {
        boolean isExistUpStation = hasStation(section.getUpStationId());
        boolean isExistDownStation = hasStation(section.getDownStationId());
        if (!isExistUpStation && !isExistDownStation) {
            throw new SectionServiceException("구간을 추가하기 위해서는 노선에 들어있는 역이 필요합니다.");
        }
        if (isExistUpStation && isExistDownStation) {
            throw new SectionServiceException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
    }

    public boolean hasStation(Long stationId) {
        return values.stream()
                .anyMatch(s -> s.getUpStationId().equals(stationId) || s.getDownStationId().equals(stationId));
    }

    public Section findLastUpSection() {
        return values.stream()
                .filter(this::isLastUpStation)
                .findAny()
                .orElseThrow();
    }

    private boolean isLastUpStation(Section section) {
        return values.stream()
                .noneMatch(s -> s.getDownStationId().equals(section.getUpStationId()));
    }

    public Section findLastDownSection() {
        return values.stream()
                .filter(this::isLastDownStation)
                .findAny()
                .orElseThrow();
    }

    private boolean isLastDownStation(Section section) {
        return values.stream()
                .noneMatch(s -> s.getUpStationId().equals(section.getDownStationId()));
    }

    private Section findNearSection(Section newSection) {
        return values.stream()
                .filter(s -> s.getUpStationId().equals(newSection.getUpStationId()) ||
                        s.getDownStationId().equals(newSection.getDownStationId()))
                .findFirst()
                .orElseThrow(() -> new SectionServiceException("중간역 생성중 기존역을 찾지 못하였습니다."));
    }

    public SectionsToBeDeletedAndUpdated delete(Long stationId) {
        validateExistStation(stationId);
        validateRemainOneSection();
        Section currentLastUpSection = findLastUpSection();
        Section currentLastDownSection = findLastDownSection();
        if (stationId.equals(currentLastUpSection.getUpStationId()) ||
                stationId.equals(currentLastDownSection.getDownStationId())) {
            return deleteLastSection(currentLastUpSection, currentLastDownSection, stationId);
        }

        Section upSideStation = extractUpSideStation(stationId);
        Section downSideStation = extractDownSideStation(stationId);
        Section sectionToBeUpdated = new Section(upSideStation.getId(), upSideStation.getUpStationId(),
                downSideStation.getDownStationId(), upSideStation.getDistance() + downSideStation.getDistance());
        return new SectionsToBeDeletedAndUpdated(downSideStation, sectionToBeUpdated);
    }

    private void validateExistStation(Long stationId) {
        if (!hasStation(stationId)) {
            throw new AccessNoneDataException("현재 라인에 존재하지 않는 역입니다.");
        }
    }

    private void validateRemainOneSection() {
        if (values.size() == 1) {
            throw new SectionServiceException("구간이 하나인 노선에서는 구간 삭제가 불가합니다.");
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

    public Section extractUpSideStation(Long stationId) {
        return values.stream()
                .filter(s -> s.getDownStationId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new SectionServiceException("중간역 삭제중 상행역을 찾지 못하였습니다."));
    }

    public Section extractDownSideStation(Long stationId) {
        return values.stream()
                .filter(s -> s.getUpStationId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new SectionServiceException("중간역 삭제중 하행역을 찾지 못하였습니다."));
    }
}
