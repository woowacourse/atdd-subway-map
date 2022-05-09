package wooteco.subway.domain;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Sections {

    private final LinkedList<Section> value;

    public Sections(final LinkedList<Section> value) {
        validateSize(value);
        this.value = value;
    }

    private void validateSize(final LinkedList<Section> value) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] 최소 한 개의 구간이 있어야 Sections 객체를 생성할 수 있습니다.");
        }
    }

    public Sections(Section first) {
        this.value = new LinkedList<>();
        value.add(first);
    }

    public List<Section> getValue() {
        return Collections.unmodifiableList(value);
    }

    public void addSection(final Station newUpStation, final Station newDownStation, final Integer distance) {
        validateStationRegistration(newUpStation, newDownStation);

        // upStation이 등록돼있다.
        if (isResistedStation(newUpStation)) {
            addDownDirectionSection(newUpStation, newDownStation, distance);
            return;
        }
        // downStation이 등록돼있다.
        addUpDirectionSection(newUpStation, newDownStation, distance);
    }

    private void validateStationRegistration(final Station newUpStation, final Station newDownStation) {
        if (!isResistedStation(newUpStation) && !isResistedStation(newDownStation)) {
            throw new IllegalArgumentException("[ERROR] 등록하려는 구간의 상행선 또는 하행선 중 한개는 노선에 존재해야합니다.");
        }

        if (isResistedStation(newUpStation) && isResistedStation(newDownStation)) {
            throw new IllegalArgumentException("[ERROR] 등록하려는 구간의 상행선 또는 하행선 중 한개만 노선에 존재해야합니다.");
        }
    }

    private boolean isResistedStation(final Station station) {
        return value.stream()
                .anyMatch(section ->
                        section.getDownStation().equals(station)
                        || section.getUpStation().equals(station)
                );
    }

    private void addDownDirectionSection(final Station newUpStation,
                                         final Station newDownStation,
                                        final Integer distance) {
        // 들어온 upStation이 상행으로 등록되어있다. -> 상행으로 등록된 구간의 하행선과의 관계를 끊고 사이에 새 하행선을 삽입
        if (isUpStation(newUpStation)) {
            splitInsertDownDirection(newUpStation, newDownStation, distance);
            return;
        }
        // 들어온 upStation이 상행으로 등록이 안돼있다(하행으로만 돼있다. 끝종점)
        value.addLast(Section.createWithoutId(newUpStation, newDownStation, distance));
    }

    private boolean isUpStation(final Station station) {
        return value.stream()
                .anyMatch(section -> section.getUpStation().equals(station));
    }

    private void splitInsertDownDirection(final Station newUpStation, final Station newDownStation, final Integer distance) {
        final Section oldSection = value.stream()
                .filter(section -> section.getUpStation().equals(newUpStation))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 해당 구간이 존재하지 않습니다."));
        final int oldSectionIndex = value.indexOf(oldSection);
        final Section frontSection = Section.createWithoutId(newUpStation, newDownStation, distance);
        final Section backSection = Section.createWithoutId(
                newDownStation,
                oldSection.getDownStation(),
                oldSection.getDistance() - distance
        );
        splitSection(oldSectionIndex, frontSection, backSection);
    }

    private void addUpDirectionSection(final Station newUpStation, final Station newDownStation,
                                       final Integer distance) {
        // 들어온 downStation이 하행으로 등록되어있다. -> 하행으로 등록된 구간의 상행선과 관계를 끊고 사이애 새 상행선을 삽입
        if (isDownStation(newDownStation)) {
            splitInsertUpDirection(newUpStation, newDownStation, distance);
            return;
        }
        // 들어온 downStation이 하행으로 등록이 안돼있다(상행으로만 있다. 시작종점)
        value.addFirst(Section.createWithoutId(newUpStation, newDownStation, distance));
    }

    private boolean isDownStation(final Station station) {
        return value.stream()
                .anyMatch(section -> section.getDownStation().equals(station));
    }

    private void splitInsertUpDirection(final Station newUpStation, final Station newDownStation,
                                        final Integer distance) {
        final Section oldSection = value.stream()
                .filter(section -> section.getDownStation().equals(newDownStation))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 해당 구간이 존재하지 않습니다."));
        final int oldSectionIndex = value.indexOf(oldSection);
        final Section frontSection = Section.createWithoutId(
                oldSection.getUpStation(),
                newUpStation,
                oldSection.getDistance() - distance);
        final Section backSection = Section.createWithoutId(newUpStation, newDownStation, distance);
        splitSection(oldSectionIndex, frontSection, backSection);
    }

    private void splitSection(final int oldSectionIndex, final Section frontSection, final Section backSection) {
        value.remove(oldSectionIndex);
        value.add(oldSectionIndex, frontSection);
        value.add(oldSectionIndex + 1, backSection);
    }
}
