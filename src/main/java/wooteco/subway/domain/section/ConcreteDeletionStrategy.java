package wooteco.subway.domain.section;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConcreteDeletionStrategy implements DeletionStrategy{
    public void delete(List<Section> sections, Long lineId, Long stationId) {
        checkDelete(sections, lineId);

        List<Section> adjacentSections = sections.stream()
                .filter(section -> lineId.equals(section.getLineId()))
                .filter(section -> section.hasStation(stationId))
                .collect(Collectors.toList());

        adjacentSections.forEach(sections::remove);
    }

    private void checkDelete(List<Section> sections, Long lineId) {
        if (isLastSection(sections, lineId)) {
            throw new IllegalArgumentException("노선의 유일한 구간은 삭제할 수 없습니다.");
        }
    }

    private boolean isLastSection(List<Section> sections, Long lineId) {
        return sections.stream()
                .filter(section -> lineId.equals(section.getLineId()))
                .count() == 1;
    }

    public Optional<Section> fixDisconnectedSection(List<Section> sections, Long lineId, Long stationId) {
        Optional<Section> upSection = sections.stream().
                filter(section -> lineId.equals(section.getLineId()) && stationId.equals(section.getDownStationId()))
                .findFirst();
        Optional<Section> downSection = sections.stream().
                filter(section -> lineId.equals(section.getLineId()) && stationId.equals(section.getUpStationId()))
                .findFirst();

        if (upSection.isEmpty() || downSection.isEmpty()) {
            return Optional.empty();
        }

        Section connectedSection = createConnectedSection(lineId, upSection.get(), downSection.get());
        sections.add(connectedSection);
        return Optional.of(connectedSection);
    }

    private Section createConnectedSection(Long lineId, Section upSection, Section downSection) {
        return new Section(lineId, upSection.getUpStationId(), downSection.getDownStationId(),
                upSection.getDistance() + downSection.getDistance());
    }
}
