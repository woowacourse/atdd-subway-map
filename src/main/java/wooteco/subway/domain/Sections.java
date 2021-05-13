package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import wooteco.subway.exception.SubwayException;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public Sections createSectionInLine(Long lineId, Long upStationId, Long downStationId,
        int distance) {
        boolean containsUpStation = containsStationInLine(sections, upStationId);
        boolean containsDownStation = containsStationInLine(sections, downStationId);

        // 상행, 하행 종점(구간) 등록
        if (isStartStation(sections, downStationId) && isEndStation(sections, upStationId)) {
            sections.add(new Section(null, lineId, upStationId, downStationId, distance));
            return new Sections(sections);
        }

        // 중간 구간 등록
        if (containsUpStation) {
            Section originSection = sections.stream()
                .filter(section -> section.getUpStationId().equals(upStationId)).findFirst().get();
            validateSectionDistance(distance, originSection);
            sections.remove(originSection);
            sections.add(new Section(
                null,
                lineId,
                originSection.getUpStationId(),
                downStationId,
                distance));
            sections.add(new Section(
                null,
                lineId,
                downStationId,
                originSection.getDownStationId(),
                originSection.getDistance() - distance));
            return new Sections(sections);
        }

        if (containsDownStation) {
            Section originSection = sections.stream()
                .filter(section -> section.getDownStationId().equals(downStationId)).findFirst()
                .get();
            validateSectionDistance(distance, originSection);
            sections.remove(originSection);
            sections.add(new Section(
                null,
                lineId,
                originSection.getUpStationId(),
                upStationId,
                originSection.getDistance() - distance));
            sections.add(new Section(
                null,
                lineId,
                upStationId,
                originSection.getDownStationId(),
                distance));
            return new Sections(sections);
        }

        throw new SubwayException("잘못된 요청입니다.");
    }

    private void validateSectionDistance(int newSectionDistance, Section originSection) {
        if (originSection.getDistance() <= newSectionDistance) {
            throw new SubwayException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
        }
    }

    private boolean containsStationInLine(List<Section> sections, Long stationId) {
        Optional<Section> foundSectionByDownStationId = sections.stream()
            .filter(section -> section.getDownStationId().equals(stationId)).findFirst();
        Optional<Section> foundSectionByUpStationId = sections.stream()
            .filter(section -> section.getUpStationId().equals(stationId)).findFirst();
        return foundSectionByUpStationId.isPresent() || foundSectionByDownStationId.isPresent();
    }

    private boolean isStartStation(List<Section> sections, Long stationId) {
        Optional<Section> upSectionOptional = sections.stream()
            .filter(section -> section.getDownStationId().equals(stationId))
            .findFirst();
        return !upSectionOptional.isPresent();
    }

    private boolean isEndStation(List<Section> sections, Long stationId) {
        Optional<Section> downSectionOptional = sections.stream()
            .filter(section -> section.getUpStationId().equals(stationId))
            .findFirst();
        return !downSectionOptional.isPresent();
    }

    public List<Section> toList() {
        return sections;
    }
}
