package wooteco.subway.domain;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Sections {

    public final int limitSectionSize = 1;
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public boolean hasOneStation(Long stationId) {
        long count = sections.stream()
                .filter(it -> it.getUpStationId().equals(stationId) || it.getDownStationId().equals(stationId))
                .count();
        return count == 1;
    }

    public Long getSectionId(Long stationId) {
        return sections.stream()
                .filter(it -> it.getUpStationId().equals(stationId) || it.getDownStationId().equals(stationId))
                .map(Section::getId)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 삭제하려는 구간이 존재하지 않습니다."));
    }

    public Section getUpStationSection(Long stationId) {
        return sections.stream()
                .filter(it -> it.getDownStationId().equals(stationId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 삭제할 대상의 상행종점을 찾을 수 없습니다."));
    }

    public Section getDownStationSection(Long stationId) {
        return sections.stream()
                .filter(it -> it.getUpStationId().equals(stationId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 삭제할 대상의 하행종점을 찾을 수 없습니다."));
    }

    public void validateAddNewSection(Section newSection) {
        validateSameStation(newSection.getUpStationId(), newSection.getDownStationId());
        validateDistance(newSection.getDistance());
        validateExistSection(newSection);
    }

    private void validateSameStation(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("[ERROR] 동일한 지하철역을 입력할 수 없습니다.");
        }
    }

    private boolean existSection(Long stationId) {
        Long count = sections.stream()
                .filter(it -> it.getUpStationId().equals(stationId) || it.getDownStationId().equals(stationId))
                .count();
        return count != 0;
    }

    private void validateExistSection(Section section) {
        if (sections.size() == 0) {
            return;
        }
        boolean hasSection1 = existSection(section.getUpStationId());
        boolean hasSection2 = existSection(section.getDownStationId());

        if (hasSection1 && hasSection2) {
            throw new IllegalArgumentException("[ERROR] 이미 등록되어 있어 추가할 수 없습니다.");
        }
        if (hasSection1 == false && hasSection2 == false) {
            throw new IllegalArgumentException("[ERROR] 지하철역이 존재하지 않습니다.");
        }
    }

    private void validateDistance(int distance) {
        if (distance < 1) {
            throw new IllegalArgumentException("[ERROR] 거리는 양수입니다.");
        }
    }

    public boolean isUpStationId(Long upStationId) {
        Long count = sections.stream()
                .filter(it -> it.getUpStationId().equals(upStationId))
                .count();
        return count != 0;
    }

    public boolean isDownStationId(Long downStationId) {
        Long count = sections.stream()
                .filter(it -> it.getDownStationId().equals(downStationId))
                .count();
        return count != 0;
    }

    public Section getSectionByUpStationId(Long upStationId) {
        return sections.stream()
                .filter(it -> it.getUpStationId().equals(upStationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 대상이 존재하지 않습니다."));
    }

    public Section getSectionByDownStationId(Long downStationId) {
        return sections.stream()
                .filter(it -> it.getDownStationId().equals(downStationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 대상이 존재하지 않습니다."));
    }

    public void validateSize() {
        if (sections.size() == limitSectionSize) {
            throw new IllegalArgumentException("[ERROR] 더 이상 삭제할 수 없습니다.");
        }
    }

    public List<Section> getSections() {
        return sections;
    }

    private List<Long> getUpStationIds() {
        return sections.stream()
                .map(it -> it.getUpStationId())
                .collect(Collectors.toList());
    }

    private List<Long> getDownStationIds() {
        return sections.stream()
                .map(it -> it.getDownStationId())
                .collect(Collectors.toList());
    }

    public Long findFinalUpStationId() {
        return getUpStationIds().stream()
                .filter(it -> !getDownStationIds().contains(it))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 상행역 종점 조회 오류가 발생했습니다."));
    }

    public boolean hasUpStationId(Long upStationId) {
        return getUpStationIds().contains(upStationId);
    }
}
