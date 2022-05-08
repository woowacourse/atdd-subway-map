package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SectionAddManager {

    private List<Section> orderedSections;

    private SectionAddManager(List<Section> orderedSections) {
        this.orderedSections = orderedSections;
    }

    public List<Section> getOrderedSections() {
        return SectionAddManager.orderFromDownStationToUpStation(orderedSections);
    }

    public static SectionAddManager of(List<Section> sections) {
        List<Section> orderedSections = orderFromDownStationToUpStation(sections);
        return new SectionAddManager(orderedSections);
    }

    private static List<Section> orderFromDownStationToUpStation(List<Section> sections) {
        List<Section> orderedSections = new ArrayList<>();
        orderedSections.add(sections.get(0));

        extendToDown(orderedSections, sections);
        extendToUp(orderedSections, sections);

        return orderedSections;
    }

    private static void extendToUp(List<Section> orderedSections, List<Section> sections) {
        Section upTerminalSection = orderedSections.get(orderedSections.size() - 1);

        Optional<Section> newUpTerminalSection = sections.stream()
                .filter(it -> it.isLinkedToDownStation(upTerminalSection))
                .findAny();

        if (newUpTerminalSection.isPresent()) {
            orderedSections.add(newUpTerminalSection.get());
            extendToUp(orderedSections, sections);
        }
    }

    private static void extendToDown(List<Section> orderedSections, List<Section> sections) {
        Section downTerminalSection = orderedSections.get(0);

        Optional<Section> newDownTerminalSection = sections.stream()
                .filter(it -> it.isLinkedToUpStation(downTerminalSection))
                .findAny();

        if (newDownTerminalSection.isPresent()) {
            orderedSections.add(0, newDownTerminalSection.get());
            extendToDown(orderedSections, sections);
        }
    }

    public List<Section> add(Section newSection) {
        orderedSections = SectionAddManager.orderFromDownStationToUpStation(orderedSections);
        validateStationsInSection(newSection);
        if (extendTerminalStationIfPossible(newSection)) {
            return orderedSections;
        }
        divideProperSection(newSection);
        return orderedSections;
    }

    private boolean extendTerminalStationIfPossible(Section newSection) {
        if (orderedSections.get(0).isLinkedToDownStation(newSection)
        || orderedSections.get(orderedSections.size() - 1).isLinkedToUpStation(newSection)) {
            orderedSections.add(newSection);
            return true;
        }
        return false;
    }

    private void divideProperSection(Section newSection) {
        Section targetSection = findTargetSection(newSection);
        orderedSections.remove(targetSection);
        List<Section> parts = targetSection.divide(newSection);
        orderedSections.addAll(parts);
    }

    private Section findTargetSection(Section newSection) {
        return orderedSections.stream()
                .filter(it -> it.ableToDivide(newSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("추가하려는 section의 역 간 거리는 존재하는 section의 역 간 거리보다 작아야 합니다."));
    }

    private void validateStationsInSection(Section section) {
        boolean downStationExist = isStationExist(section.getDownStationId());
        boolean upStationExist = isStationExist(section.getUpStationId());

        if (downStationExist == upStationExist) {
            throw new IllegalArgumentException("추가하려는 section의 역 중 하나는 기존 section에 포함되어 있어야 합니다.");
        }
    }

    private boolean isStationExist(Long stationId) {
        return orderedSections.stream()
                .anyMatch(it -> it.hasStation(stationId));
    }


}
