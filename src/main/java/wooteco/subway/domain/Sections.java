package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import wooteco.subway.exception.SectionDuplicateException;
import wooteco.subway.exception.SectionNotSuitableException;
import wooteco.subway.exception.SubwayUnknownException;
import wooteco.subway.exception.SubwayValidationException;

public class Sections {

    private static final int SINGLE_SIZE = 1;
    private static final int FIRST_INDEX = 0;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public SectionResult add(Section input) {
        validateJoinPoints(input);
        final Section joinPoint = findJoinPoint(input);

        final SectionResult sectionResult = joinPoint.sync(input);
        sections.add(input);
        return sectionResult;
    }

    private void validateJoinPoints(Section input) {
        Set<Station> stations = new HashSet<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }

        final Station upStation = input.getUpStation();
        final Station downStation = input.getDownStation();

        if (stations.containsAll(Set.of(upStation, downStation))) {
            throw new SectionDuplicateException(upStation.getName(), downStation.getName());
        }
        if (doesNotContainAny(stations, upStation, downStation)) {
            throw new SectionNotSuitableException(upStation.getName(), downStation.getName());
        }
    }

    private Section findJoinPoint(Section input) {
        Collections.sort(sections);

        if (sections.get(FIRST_INDEX).getUpStation().equals(input.getDownStation())) {
            // 상행 확장
            return sections.get(FIRST_INDEX);
        }

        final int lastIndex = sections.size() - 1;
        if (sections.get(lastIndex).getDownStation().equals(input.getUpStation())) {
            // 상행 확장
            return sections.get(lastIndex);
        }

        final List<Section> joinPoints = sections.stream()
                .filter(section -> section.isAddable(input))
                .collect(Collectors.toList());

        if (joinPoints.size() != SINGLE_SIZE) {
            throw new SubwayUnknownException("구간 추가 시도 중 예외가 발생했습니다");
        }

        return joinPoints.get(FIRST_INDEX);
    }

    private boolean doesNotContainAny(Set<Station> stations, Station upStation, Station downStation) {
        return !stations.contains(upStation) && !stations.contains(downStation);
    }

    public SectionResult remove(Long lineId, Long stationId) {
        if (hasSingleSection()) {
            return SectionResult.REMOVE_FAIL_LAST_SECTION;
        }

        final List<Section> sectionsByStationId = findSectionsByStationId(stationId);

        if (sectionsByStationId.size() == SINGLE_SIZE) {
            return reduceEndPoint(stationId, sectionsByStationId.get(FIRST_INDEX));
        }

        final Section upSection = findSection(sectionsByStationId,
                section -> section.getDownStation().isSameId(stationId));
        final Section downSection = findSection(sectionsByStationId,
                section -> section.getUpStation().isSameId(stationId));
        final Section merged = new Section(lineId, upSection.getUpStation(), downSection.getDownStation(),
                upSection.getDistance() + downSection.getDistance());

        sections.removeAll(List.of(upSection, downSection));
        sections.add(merged);

        return SectionResult.MIDDLE_REMOVED;
    }

    private List<Section> findSectionsByStationId(Long stationId) {
        final List<Section> sectionsByStationId = sections.stream()
                .filter(section -> section.hasStationById(stationId))
                .collect(Collectors.toList());

        if (sectionsByStationId.isEmpty()) {
            throw new SubwayValidationException("노선에 해당 지하철역이 존재하지 않습니다. 지하철역 ID : " + stationId);
        }

        final int foundSize = sectionsByStationId.size();
        if (foundSize != SINGLE_SIZE && foundSize != 2) {
            throw new SubwayUnknownException("노선 삭제 처리 중 노선 정보에 이상을 발견하였습니다");
        }

        return sectionsByStationId;
    }

    private Section findSection(List<Section> sections, Predicate<Section> predicate) {
        final List<Section> upSections = sections.stream()
                .filter(predicate::test)
                .collect(Collectors.toList());

        if (upSections.size() != SINGLE_SIZE) {
            throw new SubwayUnknownException("구간 삭제 중 정보에 오류가 발견되었습니다");
        }

        return upSections.get(FIRST_INDEX);
    }

    private SectionResult reduceEndPoint(Long stationId, Section section) {
        sections.remove(section);
        if (section.getUpStation().isSameId(stationId)) {
            return SectionResult.UP_REDUCED;
        }
        return SectionResult.DOWN_REDUCED;
    }

    private boolean hasSingleSection() {
        return sections.size() == SINGLE_SIZE;
    }

    public boolean isSameLineId(Long lineId) {
        return sections.get(FIRST_INDEX).isSameLineId(lineId);
    }

    public List<Section> getSections() {
        final List<Section> sections = new ArrayList<>(this.sections);
        Collections.sort(sections);
        return sections;
    }
}
