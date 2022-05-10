package wooteco.subway.domain;

import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;

import java.util.Comparator;
import java.util.List;

public class Section {
    private Long id;
    private int distance;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;

    public Section(int distance, Long lineId, Long upStationId, Long downStationId) {
        this.distance = distance;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Section(Long id, int distance, Long lineId, Long upStationId, Long downStationId) {
        this.id = id;
        this.distance = distance;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public static Section of(Line savedLine, LineRequest lineRequest) {
        return new Section(
                lineRequest.getDistance(),
                savedLine.getId(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId()
        );
    }

    public static Section of(Line line, SectionRequest sectionRequest) {
        return new Section(
                sectionRequest.getDistance(),
                line.getId(),
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId()
        );
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isExistedIn(List<Section> sections) {
        return isEquallyExistedIn(sections) || isLinearlyExistedIn(sections);
    }

    private boolean isEquallyExistedIn(List<Section> sections) {
        long count = sections.stream()
                .filter(section -> isUpStationIdEquals(section) && isDownStationIdEquals(section))
                .count();

        return count == 1;
    }

    private boolean isLinearlyExistedIn(List<Section> sections) {
        long count = sections.stream()
                .filter(section -> isUpStationIdEquals(section) || isDownStationIdEquals(section))
                .count();

        return count == 2;
    }

    private boolean isUpStationIdEquals(Section section) {
        return section.upStationId.equals(this.upStationId);
    }

    private boolean isDownStationIdEquals(Section section) {
        return section.downStationId.equals(this.downStationId);
    }

    public boolean canAddAsLastStop(Sections sections) {
        List<Long> lastStopIds = sections.getLastStopStationIds();

        return lastStopIds.contains(upStationId) || lastStopIds.contains(downStationId);
    }
}
