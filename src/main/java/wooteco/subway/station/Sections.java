package wooteco.subway.station;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Sections {
    List<Section> sections;

    public Sections(Station upStation, Station downStation, int distance) {
        this.sections = new ArrayList<>();
        sections.add(new Section(upStation, downStation, distance));
    }

    public void add(Station upStation, Station downStation, int distance) {
        // 1. XOR 검증, 동시에 상행 추가인지 하행 추가인지 확인도 가능하다.
        boolean hasUpStation = this.sections.stream()
                .anyMatch(section -> section.hasAny(upStation));
        boolean hasDownStation = this.sections.stream()
                .anyMatch(section -> section.hasAny(downStation));

        if (hasUpStation && hasDownStation) {
            throw new IllegalArgumentException("이미 등록된 역들입니다.");
        }

        if (hasUpStation) {
            // 2. upStation이 등록되어 있는 경우라면, 하행 역을 등록한다.
            Section sectionToDelete = this.sections.stream()
                    .filter(section -> section.isUpStation(upStation))
                    .findAny()
                    .get();
            int index = sections.indexOf(sectionToDelete);

            // 2-1. 거리 검증
            if (distance > sectionToDelete.getDistance()) {
                throw new IllegalArgumentException("기존 구간의 길이를 넘어서는 구간을 추가할 수 없습니다.");
            }
            // 2-2 섹션을 2개 만든다.
            Section section1 = new Section(upStation, downStation, distance);
            Section section2 = new Section(downStation,
                    sectionToDelete.getDownStation(),
                    sectionToDelete.getDistance() - distance);

            // 2-3 원래 섹션을 삭제
            sections.remove(index);

            // 2-4 추가
            sections.add(index, section2);
            sections.add(index, section1);
        }

        if (hasDownStation) {
            // 2. DownStation이 등록되어 있는 경우라면, 하행 역을 등록한다.
            Section sectionToDelete = this.sections.stream()
                    .filter(section -> section.isDownStation(downStation))
                    .findAny()
                    .get();
            int index = sections.indexOf(sectionToDelete);

            // 2-1. 거리 검증
            if (distance > sectionToDelete.getDistance()) {
                throw new IllegalArgumentException("기존 구간의 길이를 넘어서는 구간을 추가할 수 없습니다.");
            }
            // 2-2 섹션을 2개 만든다.
            Section section1 = new Section(sectionToDelete.getUpStation(), upStation, sectionToDelete.getDistance() - distance);
            Section section2 = new Section(upStation, downStation, distance);

            // 2-3 원래 섹션을 삭제
            sections.remove(index);

            // 2-4 추가
            sections.add(index, section2);
            sections.add(index, section1);
        }

    }

    public void delete(Station station) {

    }

    public Stream<Section> stream() {
        return this.sections.stream();
    }
}
