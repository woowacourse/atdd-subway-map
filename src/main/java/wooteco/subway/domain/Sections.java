package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class Sections {

    private static final int MINIMUM_SIZE = 1;

    private final List<Section> value;

    public Sections(final List<Section> value) {
        this.value = new ArrayList<>(value);
    }

    public void add(final Section section) {
        validateCanAdd(section);
        validateSection(section);

        final Optional<Section> upSection = findUpSection(section);
        final Optional<Section> downSection = findDownSection(section);

        if (upSection.isPresent()) {
            final Section targetSection = upSection.get();
            value.remove(targetSection);
            value.add(targetSection.createSectionByDownStation(section));
        }

        if (downSection.isPresent()) {
            final Section targetSection = downSection.get();
            value.remove(targetSection);
            value.add(targetSection.createSectionByUpStation(section));
        }

        value.add(section);
    }

    public void delete(final long stationId) {
        validateSize();
        final Optional<Section> targetOnDownSection = value.stream()
                .filter(section -> section.getDownStation().getId() == stationId)
                .findAny();

        final Optional<Section> targetOnUpSection = value.stream()
                .filter(section -> section.getUpStation().getId() == stationId)
                .findAny();

        if (targetOnDownSection.isEmpty() && targetOnUpSection.isEmpty()) {
            throw new IllegalArgumentException("구간에 존재하지 않는 지하철 역입니다.");
        }

        targetOnDownSection.ifPresent(value::remove);
        targetOnUpSection.ifPresent(value::remove);

        if (targetOnDownSection.isPresent() && targetOnUpSection.isPresent()) {
            final Section upSection = targetOnUpSection.get();
            final Section downSection = targetOnDownSection.get();
            value.add(downSection.mergeSection(upSection));
        }
    }

    private void validateSize() {
        if (value.size() <= MINIMUM_SIZE) {
            throw new IllegalArgumentException("구간이 " + value.size() + "개 이므로 삭제할 수 없습니다.");
        }
    }

    public boolean isBranched(final Section other) {
        final Optional<Section> upSection = findUpSection(other);
        final Optional<Section> downSection = findDownSection(other);
        return upSection.isPresent() || downSection.isPresent();
    }

    private void validateSection(final Section other) {
        validateUpSection(other);
        validateDownSection(other);
    }

    private void validateDownSection(final Section other) {
        final Optional<Section> downSection = findDownSection(other);

        downSection.ifPresent(it -> validateDistance(it, other));
    }

    private Optional<Section> findDownSection(final Section other) {
        return value.stream()
                .filter(it -> it.getDownStation().equals(other.getDownStation()))
                .findAny();
    }

    private void validateUpSection(final Section other) {
        final Optional<Section> upSection = findUpSection(other);

        upSection.ifPresent(it -> validateDistance(it, other));
    }

    private Optional<Section> findUpSection(final Section other) {
        return value.stream()
                .filter(it -> it.getUpStation().equals(other.getUpStation()))
                .findAny();
    }

    private void validateDistance(final Section section, final Section other) {
        if (other.isGreaterOrEqualTo(section)) {
            throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 구간 거리보다 적어야 합니다.");
        }
    }

    private void validateCanAdd(final Section other) {
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

    public List<Section> getSections() {
        return value;
    }

    @Override
    public String toString() {
        return "Sections{" +
                "value=" + value +
                '}';
    }
}
