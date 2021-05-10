package wooteco.subway.line.domain;

import wooteco.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    // TODO :
    //  존재하면 sectionAddRequest의 upstationId로 section을 찾고
    //  찾은 section의 distance가 sectionAddRequest의 distance보다 작거나 같은 경우는 예외다.
    //  찾은 section의 upstationId를 sectionAddRequest의 downStationId로 수정한다.
    //  찾은 section의 distance를 sectionAddRequest의 distance를 뺀 값으로 수정한다.

    // TODO :
    //  존재하면 sectionAddRequest의 downStationId로 section을 찾고
    //  찾은 section의 distance가 sectionAddRequest의 distance보다 작거나 같은 경우는 예외다.
    //  찾은 section의 downStationId를 sectionAddRequest의 upStationId로 수정한다.
    //  찾은 section의 distance를 sectionAddRequest의 distance를 뺀 값으로 수정한다.


    public void add(Section section) {
        checkAbleToAddSection(section);
        sections.add(section);
    }

    private Optional<Section> findTargetSection(Station sourceStation) {
        return sections.stream()
                .filter(section -> section.has(sourceStation))
                .findFirst();
    }

    private void checkAbleToAddSection(Section section) {
        if (!isOnlyOneRegistered(section)) {
            throw new IllegalStateException("[ERROR] 노선에 등록할 구간의 역이 하나만 등록되어 있어야 합니다.");
        }
    }

    public boolean isOnlyOneRegistered(Section anotherSection) {
        boolean hasUpStation = sections.stream()
                .anyMatch(section -> section.has(anotherSection.upStation()));
        boolean hasDownStation = sections.stream()
                .anyMatch(section -> section.has(anotherSection.downStation()));
        return (hasUpStation && !hasDownStation) || (!hasUpStation && hasDownStation);
    }

    public List<Section> sections() {
        return sections;
    }
}
