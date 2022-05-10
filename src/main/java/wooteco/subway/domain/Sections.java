package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;


    public Sections(List<Section> sections) {
        this.sections = sections;
    }


    public void validateSection(long upStationId, long downStationId, int distance) {
        if (existByStationId(upStationId)
                && existByStationId(downStationId)) {
            throw new IllegalArgumentException("상행, 하행이 대상 노선에 둘 다 존재합니다.");
        }

        if (!existByStationId(upStationId)
                && !existByStationId(downStationId)) {
            throw new IllegalArgumentException("상행, 하행이 대상 노선에 둘 다 존재하지 않습니다.");
        }

        if ((existByDownStationId(downStationId) && findDistanceById(findIdByDownStationId(downStationId)) <= distance)
                || (existByUpStationId(upStationId)
                && findDistanceById(findIdByUpStationId(upStationId)) <= distance)) {
            throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우, 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
        }
    }

    private boolean existByStationId(long stationId) {
        return existByUpStationId(stationId) || existByDownStationId(stationId);
    }

    private boolean existByUpStationId(long stationId) {
        return sections.stream()
                .map(Section::getUpStationId)
                .anyMatch(id -> id == stationId);
    }

    private boolean existByDownStationId(long stationId) {
        return sections.stream()
                .map(Section::getDownStationId)
                .anyMatch(id -> id == stationId);
    }

    private long findIdByUpStationId(long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId() == stationId)
                .map(Section::getId)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("ㅋㅋ"));
    }

    private long findIdByDownStationId(long stationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId() == stationId)
                .map(Section::getId)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("ㅋㅋ"));
    }

    private int findDistanceById(Long id) {
        return sections.stream()
                .filter(section -> section.getId().equals(id))
                .findAny()
                .map(Section::getDistance)
                .orElseThrow(() -> new IllegalArgumentException("없음"));
    }

    public Section find겹치는SectionId(long upStationId, long downStationId, int distance) {
        if (existByUpStationId(upStationId)) {
            Section section = findById(findIdByUpStationId(upStationId));
            return new Section(
                    section.getId(), section.getLineId(), downStationId, section.getDownStationId(),
                    section.getDistance() - distance, section.getLineOrder());
        }
        if (existByDownStationId(downStationId)) {
            Section section = findById(findIdByDownStationId(downStationId));
            return new Section(
                    section.getId(), section.getLineId(), section.getUpStationId(), upStationId,
                    section.getDistance() - distance, section.getLineOrder());
        }
        if (existByUpStationId(downStationId)) {
            Section section = findById(findIdByUpStationId(downStationId));
            return new Section(
                    null, section.getLineId(), upStationId, downStationId,
                    distance, section.getLineOrder() + 1);
        }
        Section section = findById(findIdByDownStationId(upStationId));
        return new Section(
                null, section.getLineId(), upStationId, downStationId,
                distance, section.getLineOrder());
    }

    private Section findById(long sectionId) {
        return sections.stream()
                .filter(section -> section.getId() == sectionId)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 Section이 존재하지 않습니다.똑바로 해라"));
    }

    public List<Long> getStationsId() {
        List<Section> sortedSections = createSortedSection();

        List<Long> stationsId = new ArrayList<>();
        for (Section section : sortedSections) {
            stationsId.add(section.getUpStationId());
        }
        stationsId.add(sortedSections.get(sortedSections.size() - 1).getDownStationId());

        return stationsId;
    }

    private List<Section> createSortedSection() {
        return sections.stream()
                .sorted(Comparator.comparingLong(Section::getLineOrder))
                .collect(Collectors.toList());
    }

    public boolean hasTwoSection() {
        return sections.size() == 2;
    }

    public Section getDeleteSection() {
        return sections.get(0);
    }

    public Section getUpsideSection() {
        return createSortedSection().get(0);
    }

    public Section getDownsideSection() {
        return createSortedSection().get(1);
    }
}
