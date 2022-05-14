package wooteco.subway.domain;

import wooteco.subway.exception.SectionServiceException;

import java.util.List;
import java.util.Optional;

public class Sections {

    private final List<Section> values;

    public Sections(List<Section> values) {
        this.values = values;
    }

    public Optional<Section> add(Section newSection) {
        validateExistStationInLine(newSection);
        Long currentLastUpStationId = findLastUpStationId();
        Long currentLastDownStationId = findLastDownStationId();

        if (newSection.isNewLastStation(currentLastUpStationId, currentLastDownStationId)) {
            values.add(newSection);
            return Optional.empty();
        }

        Section existNearSection = findNearSection(newSection);
        if (newSection.getDistance() >= existNearSection.getDistance()) {
            throw new SectionServiceException("새로운 구간의 길이는 기존 역 사이의 길이보다 작아야 합니다.");
        }
        Section sectionThatNeedToBeUpdated = new Section(existNearSection.getId(), existNearSection.getLineId(),
                newSection.getUpStationId(), existNearSection.getDownStationId(),
                existNearSection.getDistance() - newSection.getDistance());

        return Optional.of(sectionThatNeedToBeUpdated);
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

    public Long findLastUpStationId() {
        return values.stream()
                .filter(this::isLastUpStation)
                .map(Section::getUpStationId)
                .findAny()
                .orElseThrow();
    }

    private boolean isLastUpStation(Section section) {
        return values.stream()
                .noneMatch(s -> s.getDownStationId().equals(section.getUpStationId()));
    }

    public Long findLastDownStationId() {
        return values.stream()
                .filter(this::isLastDownStation)
                .map(Section::getDownStationId)
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


    //////

    public boolean isLastStation(Long newUpStationId, Long newDownStationId) {
        return isLastUpStation(newDownStationId) || isLastDownStation(newUpStationId);
    }

    private boolean isLastUpStation(Long stationId) {
        boolean notExitInLine = values.stream()
                .anyMatch(s -> s.getDownStationId().equals(stationId) || s.getUpStationId().equals(stationId));
        if (!notExitInLine) {
            return false;
        }
        return values.stream()
                .noneMatch(s -> s.getDownStationId().equals(stationId));
    }

    private boolean isLastDownStation(Long stationId) {
        boolean notExitInLine = values.stream()
                .anyMatch(s -> s.getDownStationId().equals(stationId) || s.getUpStationId().equals(stationId));
        if (!notExitInLine) {
            return false;
        }
        return values.stream()
                .noneMatch(s -> s.getUpStationId().equals(stationId));
    }

    public boolean hasOneSection() {
        return values.size() <= 1;
    }

    public Optional<Section> checkAndExtractLastStation(Long stationId) {
        if (isLastUpStation(stationId)) {
            return values.stream()
                    .filter(s -> s.getUpStationId().equals(stationId))
                    .findFirst();
        }
        if (isLastDownStation(stationId)) {
            return values.stream()
                    .filter(s -> s.getDownStationId().equals(stationId))
                    .findFirst();
        }
        return Optional.empty();
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

    public List<Section> getValues() {
        return List.copyOf(values);
    }
}
