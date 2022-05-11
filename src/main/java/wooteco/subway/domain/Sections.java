package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import wooteco.subway.exception.SectionDuplicateException;
import wooteco.subway.exception.SectionNotSuitableException;
import wooteco.subway.exception.SubwayUnknownException;

public class Sections {

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
        final List<Section> joinPoints = sections.stream()
                .filter(section -> section.isAddable(input))
                .collect(Collectors.toList());

        if (joinPoints.size() != 1) {
            throw new SubwayUnknownException("구간 추가 시도 중 예외가 발생했습니다");
        }

        return joinPoints.get(0);
    }

    private boolean doesNotContainAny(Set<Station> stations, Station upStation, Station downStation) {
        return !stations.contains(upStation) && !stations.contains(downStation);
    }

    public SectionResult remove(Section other) {
        if (hasSingleSection()) {
            return SectionResult.REMOVE_FAIL_LAST_SECTION;
        }

        sections.remove(other);

        return SectionResult.REMOVE_SUCCESS;
    }

    private boolean hasSingleSection() {
        return sections.size() == 1;
    }
}
