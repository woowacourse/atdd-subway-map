package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {
    private static final String DUPLICATED_SECTION_ERROR_MESSAGE = "중복된 구간입니다.";
    private static final String LINK_FAILURE_ERROR_MESSAGE = "해당 구간은 역과 연결될 수 없습니다.";
    private static final String NO_NEXT_SECTION_ERROR_MESSAGE = "해당 하행과 상행으로 연결되는 구간이 없습니다.";
    private static final String ONE_LESS_SECTION_ERROR_MESSAGE = "해당 지하철 노선은 1개 이하의 구간을 가지고 있어 역을 삭제할 수 없습니다.";
    private static final String NO_UP_STATION_ERROR_MESSAGE = "해당 상행역의 구간이 없습니다";
    private static final String NO_DOWN_STATION_ERROR_MESSAGE = "해당 하행역의 구간이 없습니다";
    private static final String SECTION_LENGTH_ERROR_MESSAGE = "새 구간의 길이가 기존 역 사이 길이보다 작아야 합니다.";
    private static final int COMBINED_UP_STATION_INDEX = 0;
    private static final int COMBINED_DOWN_STATION_INDEX = 2;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections forSave(List<Section> sections, Section inSection) {
        Sections result = new Sections(sections);
        if (result.hasUpStation(inSection) && result.hasDownStation(inSection)) {
            throw new IllegalArgumentException(DUPLICATED_SECTION_ERROR_MESSAGE);
        }
        if (!result.sections.isEmpty() && result.hasNoStation(inSection) && result.hasNoStation(inSection.toReverse())) {
            throw new IllegalArgumentException(LINK_FAILURE_ERROR_MESSAGE);
        }
        return result;
    }

    public static Sections forDelete(List<Section> sections) {
        Sections result = new Sections(sections);
        if (result.sections.size() <= 1) {
            throw new IllegalArgumentException(ONE_LESS_SECTION_ERROR_MESSAGE);
        }
        return result;
    }

    public List<Station> calculateStations() {
        final List<Station> stations = new ArrayList<>();
        Station nowStation = getFirst().getUpStation();
        stations.add(nowStation);
        while (isAnyLink(nowStation)) {
            nowStation = getNext(nowStation);
            stations.add(nowStation);
        }
        return stations;
    }

    public Section findCombinedLink(Station middleStation) {
        List<Section> combinedSections = findLinks(middleStation);
        List<Station> combined = new Sections(combinedSections).calculateStations();
        int distance = combinedSections.stream().map(Section::getDistance).reduce(Integer::sum).orElseThrow();
        return new Section(sections.get(0).getLineId(),
                combined.get(COMBINED_UP_STATION_INDEX),
                combined.get(COMBINED_DOWN_STATION_INDEX), distance);
    }

    public List<Section> findLinks(Station station) {
        return sections.stream().filter(section -> section.contains(station)).collect(Collectors.toList());
    }

    public Optional<Long> findSide(Station station) {
        if (isFirst(station)) {
            return Optional.of(findByUpStation(station).getId());
        }
        if (isLast(station)) {
            return Optional.of(findByDownStation(station).getId());
        }
        return Optional.empty();
    }

    public Optional<Section> findUpdateResult(Section section) {
        if (isMiddleUpAttach(section)) {
            Section upBase = findByUpStation(section.getUpStation());
            validateDistance(upBase, section);
            return Optional.of(upBase.toRemain(section));
        }
        if (isMiddleDownAttach(section)) {
            Section downBase = findByDownStation(section.getDownStation());
            validateDistance(downBase, section);
            return Optional.of(downBase.toRemain(section));
        }
        return Optional.empty();
    }

    private void validateDistance(Section base, Section inSection) {
        if (inSection.getDistance() >= base.getDistance()) {
            throw new IllegalArgumentException(SECTION_LENGTH_ERROR_MESSAGE);
        }
    }

    private boolean isAnyLink(Station downStation) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().equals(downStation));
    }

    private Section getFirst() {
        return sections.stream()
                .filter(section -> isFirst(section.getUpStation())).findFirst().orElse(sections.get(0));
    }

    private Station getNext(Station now) {
        return sections.stream()
                .filter(section -> now.equals(section.getUpStation())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NO_NEXT_SECTION_ERROR_MESSAGE)).getDownStation();
    }

    private boolean hasNoStation(Section section) {
        return !hasUpStation(section) && !hasDownStation(section);
    }

    private boolean hasUpStation(Section inSection) {
        return sections.stream()
                .anyMatch(section -> section.getUpStationId().equals(inSection.getUpStationId()));
    }

    private boolean hasDownStation(Section inSection) {
        return sections.stream()
                .anyMatch(section -> section.getDownStationId().equals(inSection.getDownStationId()));
    }

    private Section findByUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station)).findFirst()
                .orElseThrow(() -> new IllegalStateException(NO_UP_STATION_ERROR_MESSAGE));
    }

    private Section findByDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(station)).findFirst()
                .orElseThrow(() -> new IllegalStateException(NO_DOWN_STATION_ERROR_MESSAGE));
    }

    private boolean isMiddleUpAttach(Section inSection) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().equals(inSection.getUpStation()));
    }

    private boolean isMiddleDownAttach(Section inSection) {
        return sections.stream()
                .anyMatch(section -> section.getDownStation().equals(inSection.getDownStation()));
    }

    private boolean isFirst(Station station) {
        return sections.stream()
                .noneMatch(section -> section.getDownStation().equals(station));
    }

    private boolean isLast(Station station) {
        return sections.stream()
                .noneMatch(section -> section.getUpStation().equals(station));
    }
}
