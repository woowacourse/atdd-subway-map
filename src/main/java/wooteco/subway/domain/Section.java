package wooteco.subway.domain;

import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.SectionResult;

import java.util.List;
import java.util.Optional;

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

    public static Section createBySections(Section existedSection, Section insertedSection) {
        int generatedDistance = existedSection.getDistance() - insertedSection.distance;
        if (existedSection.upStationId.equals(insertedSection.upStationId)) {
            return new Section(generatedDistance, existedSection.getLineId(), insertedSection.downStationId, existedSection.downStationId);
        }
        if (existedSection.downStationId.equals(insertedSection.downStationId)) {
            return new Section(generatedDistance, existedSection.getLineId(), existedSection.upStationId, insertedSection.upStationId);
        }
        throw new IllegalArgumentException("만들 수 없는 구간입니다.");
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

    public boolean canAddAsLastStation(Sections sections) {
        List<Long> lastStopIds = sections.getLastStationIds();

        return lastStopIds.contains(upStationId) || lastStopIds.contains(downStationId);
    }

    public SectionResult canAddAsBetweenStation(Sections sections) {
        // 구간이 존재하고 && 거리가 되는지
        Optional<Section> upStationSection = sections.getExistedUpStationSection(upStationId);

        if (upStationSection.isPresent() && upStationSection.get().getDistance() > distance) {
            return SectionResult.of(upStationSection.get(), this);
        }
        Optional<Section> downStationSection = sections.getExistedDownStationSection(downStationId);
        if (downStationSection.isPresent() && downStationSection.get().getDistance() > distance) {
            return SectionResult.of(downStationSection.get(), this);
        }
        return new SectionResult(false);
    }
}
