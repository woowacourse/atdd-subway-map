package wooteco.subway.line.domain;

import wooteco.subway.common.exception.AlreadyExistsException;
import wooteco.subway.common.exception.InvalidInputException;
import wooteco.subway.common.exception.NotFoundException;
import wooteco.subway.station.domain.Station;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {
    private static final int MINIMUM_SIZE = 1;
    private final List<Section> sections;

    public Sections() {
        this(new ArrayList<>());
    }

    public Sections(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void addSection(final Section targetSection) {
        if (sections.isEmpty()) {
            sections.add(targetSection);
            return;
        }
        validateDuplicationStation(targetSection.upStation(), targetSection.downStation());
        validateContain(targetSection.upStation(), targetSection.downStation());

        addSectionUpAndUp(targetSection);
        addSectionDownAndDown(targetSection);
        sections.add(targetSection);
    }

    public List<Section> sortedSections() {
        Section section = headSection();
        List<Section> targetSections = new LinkedList<>();
        targetSections.add(section);

        while (nextSection(section).isPresent()) {
            section = nextSection(section).get();
            targetSections.add(section);
        }
        return targetSections;
    }

    private Optional<Section> nextSection(final Section section) {
        return sections.stream()
                .filter(nextSection -> nextSection.sameUpStation(section.downStation()))
                .findFirst();
    }

    private void addSectionDownAndDown(final Section targetSection) {
        sections.stream()
                .filter(section -> section.sameUpStation(targetSection.upStation()))
                .findFirst()
                .ifPresent(section -> changeUpAndDown(section, targetSection));
    }

    private void addSectionUpAndUp(final Section targetSection) {
        sections.stream()
                .filter(section -> section.sameDownStation(targetSection.downStation()))
                .findFirst()
                .ifPresent(section -> changeDownAndUp(section, targetSection));
    }

    private void changeUpAndDown(final Section section, final Section targetSection) {
        if (section.distance() <= targetSection.distance()) {
            throw new InvalidInputException("기존의 구간보다 길이가 길 수 없음!!");
        }
        sections.remove(section);
        sections.add(new Section(section.line(), targetSection.downStation(), section.downStation(), section.distance() - targetSection.distance()));
    }

    private void changeDownAndUp(final Section section, final Section targetSection) {
        if (section.distance() <= targetSection.distance()) {
            throw new InvalidInputException("기존의 구간보다 길이가 길 수 없음!!");
        }
        sections.remove(section);
        sections.add(new Section(section.line(), section.upStation(), targetSection.upStation(), section.distance() - targetSection.distance()));
    }

    public void deleteStation(final Station station) {
        validateSize();
        Optional<Section> upSection = findSectionByUpStation(station);
        Optional<Section> downSection = findSectionByDownStation(station);

        if (!upSection.isPresent() || !downSection.isPresent()) {
            deleteUpEndPointStation(station);
            deleteDownEndPointStation(station);
            return;
        }
        changeSection(upSection.get(), downSection.get());
    }

    public int size() {
        return sections.size();
    }

    private void changeSection(final Section upSection, final Section downSection) {
        this.sections.remove(upSection);
        this.sections.remove(downSection);

        this.sections.add(new Section(
                downSection.line(),
                downSection.upStation(),
                upSection.downStation(),
                upSection.distance() + downSection.distance()));
    }

    private void validateSize() {
        if (sections.size() == MINIMUM_SIZE) {
            throw new InvalidInputException("1개의 구간만 있기에 삭제 할 수 없음!");
        }
    }

    private boolean existSection(final Station upStation, final Station downStation) {
        List<Station> stations = stations();
        List<Station> targetStation = Arrays.asList(upStation, downStation);
        return stations.containsAll(targetStation);
    }

    private boolean containsStation(final Station upStation, final Station downStation) {
        List<Station> stations = stations();
        return stations.contains(upStation) || stations.contains(downStation);
    }

    private List<Station> stations() {
        return sections.stream()
                .flatMap(section ->
                        Stream.of(
                                section.upStation(),
                                section.downStation()
                        ))
                .distinct()
                .collect(Collectors.toList());
    }

    private void deleteDownEndPointStation(final Station station) {
        findSectionByDownStation(station)
                .ifPresent(this.sections::remove);
    }

    private void deleteUpEndPointStation(final Station station) {
        findSectionByUpStation(station)
                .ifPresent(this.sections::remove);
    }

    private Section headSection() {
        return sections.stream()
                .filter(section -> !anyMatches(section))
                .findFirst()
                .orElseThrow(() -> new InvalidInputException("구간이 제대로 등록되어있지 않음!"));
    }

    private boolean anyMatches(final Section section) {
        return sections.stream()
                .filter(it -> !it.equals(section))
                .anyMatch(it -> it.sameDownStation(section.upStation()));
    }

    private Optional<Section> findSectionByUpStation(final Station station) {
        return sections.stream()
                .filter(section -> section.sameUpStation(station))
                .findFirst();
    }

    private Optional<Section> findSectionByDownStation(final Station station) {
        return sections.stream()
                .filter(section -> section.sameDownStation(station))
                .findFirst();
    }

    private void validateDuplicationStation(final Station upStation, final Station downStation) {
        if (existSection(upStation, downStation)) {
            throw new AlreadyExistsException("이미 등록되어 있는 구간임!");
        }
    }

    private void validateContain(final Station upStation, final Station downStation) {
        if (!containsStation(upStation, downStation)) {
            throw new NotFoundException("상행, 하행역 둘다 노선에 등록되어 있지 않음!!");
        }
    }
}
