package wooteco.subway.domain;

import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.SectionResult;
import wooteco.subway.exception.section.IllegalMergeSectionException;

import java.util.List;
import java.util.Optional;

public class Section {
    private static final int EXACTLY_SAME_COUNT = 1;
    private static final int LINEARLY_SAME_COUNT = 2;

    private Long id;
    private final int distance;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;

    public Section(int distance, Long lineId, Long upStationId, Long downStationId) {
        this.distance = distance;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Section(Long id, int distance, Long lineId, Long upStationId, Long downStationId) {
        this(distance, lineId, upStationId, downStationId);
        this.id = id;
    }

    public static Section of(Line line, LineRequest lineRequest) {
        return new Section(
                lineRequest.getDistance(),
                line.getId(),
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
        throw new IllegalMergeSectionException();
    }

    public boolean canAddAsLastStation(Sections sections) {
        List<Long> lastStationIds = sections.getLastStationIds();

        return lastStationIds.contains(upStationId) || lastStationIds.contains(downStationId);
    }

    public SectionResult canAddAsBetweenStation(Sections sections) {
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

    public boolean isEqualDownStationId(Long stationId) {
        return downStationId.equals(stationId);
    }

    public boolean isEqualUpStationId(Long stationId) {
        return upStationId.equals(stationId);
    }

    public boolean isExistedIn(List<Section> sections) {
        return isEquallyExistedIn(sections) || isLinearlyExistedIn(sections);
    }

    private boolean isEquallyExistedIn(List<Section> sections) {
        long count = sections.stream()
                .filter(section -> isUpStationIdEquals(section) && isDownStationIdEquals(section))
                .count();

        return count == EXACTLY_SAME_COUNT;
    }

    private boolean isLinearlyExistedIn(List<Section> sections) {
        long count = sections.stream()
                .filter(section -> isUpStationIdEquals(section) || isDownStationIdEquals(section))
                .count();

        return count == LINEARLY_SAME_COUNT;
    }

    private boolean isUpStationIdEquals(Section section) {
        return section.upStationId.equals(this.upStationId);
    }

    private boolean isDownStationIdEquals(Section section) {
        return section.downStationId.equals(this.downStationId);
    }

    public Long getId() {
        return id;
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
}
