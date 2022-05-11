package wooteco.subway.domain;

import wooteco.subway.exception.SectionServiceException;

import java.util.List;
import java.util.Optional;

public class Sections {

    private final List<Section> values;

    public Sections(List<Section> values) {
        this.values = values;
    }

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

    public Section findNearSection(Long newUpStationId, Long newDownStationId) {
        return values.stream()
                .filter(s -> s.getUpStationId().equals(newUpStationId) || s.getDownStationId().equals(newDownStationId))
                .findFirst()
                .orElseThrow(() -> new SectionServiceException("중간역 생성중 기존역을 찾지 못하였습니다."));
    }

    public boolean hasStation(Long stationId) {
        return values.stream()
                .anyMatch(s -> s.getUpStationId().equals(stationId) || s.getDownStationId().equals(stationId));
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
}
