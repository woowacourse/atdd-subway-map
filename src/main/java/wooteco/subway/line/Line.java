package wooteco.subway.line;


import java.util.Collections;
import java.util.List;
import java.util.Objects;
import wooteco.subway.exception.service.ValidationFailureException;
import wooteco.subway.line.section.Section;
import wooteco.subway.line.section.Sections;

public class Line {

    private final Long id;
    private final String name;
    private final String color;
    private final Sections sections;

    public Line(final String name, final String color) {
        this(null, name, color, Collections.emptyList());
    }

    public Line(final Long id, final String name, final String color) {
        this(id, name, color, Collections.emptyList());
    }

    public Line(final Long id, final String name, final String color, final List<Section> sectionGroup) {
        this(id, name, color, new Sections(sectionGroup));
    }

    public Line(final Long id, final String name, final String color, final Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public void validateStationsToAddSection(final Long upStationId, final Long downStationId) {
        sections.validateBothExistentStation(upStationId, downStationId);
        sections.validateNoneExistentStation(upStationId, downStationId);
    }

    public boolean includesTerminalStation(final Long upStationId, final Long downStationId) {
        return isStartStation(downStationId) || isLastStation(upStationId);
    }

    public boolean isTerminalStation(final Long stationId) {
        return isStartStation(stationId) || isLastStation(stationId);
    }

    private boolean isStartStation(final Long downStationId) {
        return sections.isFirstStationId(downStationId);
    }

    private boolean isLastStation(final Long upStationId) {
        return sections.isLastStationId(upStationId);
    }

    public Section findUpdatedTarget(final Long upStationId, final Long downStationId, final int distance) {
        final Section target = sections.findSameForm(upStationId, downStationId);
        target.validateSmaller(distance);
        return target;
    }

    public Section findSectionHasUpStation(long existentStationId) {
        return sections.findSectionHasUpStation(existentStationId);
    }

    public Section findSectionHasDownStation(long existentStationId) {
        return sections.findSectionHasDownStation(existentStationId);
    }

    public void validateSizeToDeleteSection() {
        if (sections.size() <= 1) {
            throw new ValidationFailureException("구간이 한 개 이하면 지울 수 없습니다.");
        }
    }

    public Section findTerminalSection(Long stationId) {
        if (isStartStation(stationId)) {
            return sections.getFirstSection();
        }
        if (isLastStation(stationId)) {
            return sections.getLastSection();
        }
        throw new ValidationFailureException("해당역은 종점이 아닙니다.");
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Sections getSections() {
        return sections;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Line line = (Line) o;
        return Objects.equals(getId(), line.getId()) && Objects
            .equals(getName(), line.getName()) && Objects.equals(getColor(), line.getColor())
            && Objects.equals(getSections(), line.getSections());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getColor(), getSections());
    }
}
