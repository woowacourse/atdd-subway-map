package wooteco.subway.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class Sections {
    private final List<Section> sections;

    public Optional<Section> save(Section section) {
        checkSavable(section);

        Optional<Section> fixedSection = fixOverLappedSection(section);

        sections.add(section);

        return fixedSection;
    }

    public Optional<Section> fixOverLappedSection(Section section) {
        Optional<Section> overLappedSection = getSectionOverLappedBy(section);
        Section revisedSection = null;

        if (overLappedSection.isPresent()) {
            overLappedSection.ifPresent(sections::remove);
            revisedSection = overLappedSection.get().revisedBy(section);
            sections.add(revisedSection);
        }

        return Optional.ofNullable(revisedSection);
    }

    private void checkSavable(Section section) {
        checkExistence(section);
        checkConnected(section);
        checkDistance(section);
    }

    private void checkExistence(Section section) {
        if (isLineAlreadyHasBothStationsOf(section)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 모두 존재합니다.");
        }
    }

    private boolean isLineAlreadyHasBothStationsOf(Section section) {
        Long lineId = section.getLineId();

        return existByLineAndStation(lineId, section.getUpStationId())
                && existByLineAndStation(lineId, section.getDownStationId());
    }

    private boolean existByLineAndStation(Long lineId, Long stationId) {
        return sections.stream()
                .filter(section -> lineId.equals(section.getLineId()))
                .anyMatch(section -> section.hasStation(stationId));
    }

    private void checkConnected(Section section) {
        if (!existConnectedTo(section)) {
            throw new IllegalArgumentException("기존 노선과 연결된 구간이 아닙니다.");
        }
    }

    private boolean existConnectedTo(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.isConnectedTo(newSection));
    }

    private void checkDistance(Section newSection) {
        Optional<Section> overLappedSection = getSectionOverLappedBy(newSection);

        if (overLappedSection.isPresent() && newSection.isLongerThan(overLappedSection.get())) {
            throw new IllegalArgumentException("적절한 거리가 아닙니다.");
        }
    }

    private Optional<Section> getSectionOverLappedBy(Section newSection) {
        return sections.stream()
                .filter(section -> section.isOverLappedWith(newSection))
                .findFirst();
    }

    public Optional<Section> delete(Long lineId, Long stationId) {
        checkDelete(lineId);

        Optional<Section> connectedSection = fixDisconnectedSection(lineId, stationId);

        List<Section> adjacentSections = sections.stream()
                .filter(section -> lineId.equals(section.getLineId()))
                .filter(section -> section.hasStation(stationId))
                .collect(Collectors.toList());

        adjacentSections.forEach(sections::remove);

        return connectedSection;
    }

    private void checkDelete(Long lineId) {
        if (isLastSection(lineId)) {
            throw new IllegalArgumentException("노선의 유일한 구간은 삭제할 수 없습니다.");
        }
    }

    private boolean isLastSection(Long lineId) {
        return sections.stream()
                .filter(section -> lineId.equals(section.getLineId()))
                .count() == 1;
    }

    public Optional<Section> fixDisconnectedSection(Long lineId, Long stationId) {

        Optional<Section> upSection = sections.stream().
                filter(section -> lineId.equals(section.getLineId()) && stationId.equals(section.getDownStationId()))
                .findFirst();
        Optional<Section> downSection = sections.stream().
                filter(section -> lineId.equals(section.getLineId()) && stationId.equals(section.getUpStationId()))
                .findFirst();

        Section connectedSection = null;

        if (upSection.isPresent() && downSection.isPresent()) {
            connectedSection = createConnectedSection(lineId, upSection.get(), downSection.get());
            sections.add(connectedSection);
        }

        return Optional.ofNullable(connectedSection);
    }

    private Section createConnectedSection(Long lineId, Section upSection, Section downSection) {
        return new Section(lineId, upSection.getUpStationId(), downSection.getDownStationId(),
                upSection.getDistance() + downSection.getDistance());
    }
}
