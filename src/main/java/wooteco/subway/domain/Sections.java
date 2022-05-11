package wooteco.subway.domain;

import java.util.HashSet;
import java.util.List;

public class Sections {

    private final List<Section> value;

    public Sections(final List<Section> value) {
        this.value = value;
    }

    public void add(final Section section) {
        validateExist(section);
    }

    private void validateExist(final Section other) {
        final HashSet<Station> stations = new HashSet<>();

        for (Section section : value) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }

        final boolean hasUpStation = stations.contains(other.getUpStation());
        final boolean hasDownStation = stations.contains(other.getDownStation());
        if (hasUpStation && hasDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
        }
        if (!hasUpStation && !hasDownStation) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 구간을 추가할 수 없습니다.");
        }
    }
}
