package wooteco.subway.line.domain;

import wooteco.subway.common.exception.InvalidInputException;
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

    public void addSection(final Section section) {
        this.sections.add(section);
    }

    public List<Section> sections() {
        return Collections.unmodifiableList(sections);
    }

    public List<Section> sortedSections() {
        List<Section> sortedSections = new LinkedList<>();
        Section headSection = headSection();
        sortedSections.add(headSection);
        for (int i = 0; i < sections.size(); i++) {
            Section finalHeadSection = headSection;
            Optional<Section> findSection = sections.stream()
                    .filter(section -> !section.equals(finalHeadSection))
                    .filter(section -> section.upStation().equals(finalHeadSection.downStation()))
                    .findFirst();
            if (findSection.isPresent()) {
                sortedSections.add(findSection.get());
                headSection = findSection.get();
            }
        }
        return Collections.unmodifiableList(sortedSections);
    }

    public void upwardEndPointRegistration(final Section targetSection) {
        Section headSection = headSection();
        if (headSection.sameUpStation(targetSection.downStation())) {
            this.sections.add(targetSection);
        }
    }

    public void downwardEndPointRegistration(final Section targetSection) {
        Section tailSection = tailSection();
        if (tailSection.sameDownStation(targetSection.upStation())) {
            this.sections.add(targetSection);
        }
    }

    public void betweenUpwardRegistration(final Section targetSection) {
        Section findSection = findByUpStationSection(targetSection.upStation());
        if (Objects.isNull(findSection)) {
            return;
        }
        validateDistance(findSection.distance(), targetSection.distance());

        this.sections.remove(findSection);
        this.sections.add(targetSection);
        this.sections.add(new Section(targetSection.line(), targetSection.downStation(), findSection.downStation(),
                findSection.distance() - targetSection.distance()));
    }

    public void betweenDownwardRegistration(final Section targetSection) {
        Section findSection = findByDownStationSection(targetSection.downStation());
        if (Objects.isNull(findSection)) {
            return;
        }
        validateDistance(findSection.distance(), targetSection.distance());

        this.sections.remove(findSection);
        this.sections.add(targetSection);
        this.sections.add(new Section(targetSection.line(), findSection.upStation(), targetSection.upStation(),
                findSection.distance() - targetSection.distance()));
    }

    public void deleteStation(final Station station) {
        validateSize();
        Section upSection = findByUpStationSection(station);
        Section downSection = findByDownStationSection(station);

        if (!Objects.isNull(upSection) && !Objects.isNull(downSection)) {
            this.sections.remove(upSection);
            this.sections.remove(downSection);

            this.sections.add(new Section(
                    downSection.line(),
                    downSection.upStation(),
                    upSection.downStation(),
                    upSection.distance() + downSection.distance()));
            return;
        }
        deleteUpwardEndPointStation(station);
        deleteDownwardEndPointStation(station);
    }

    private void validateSize() {
        if (sections.size() == MINIMUM_SIZE) {
            throw new InvalidInputException("1개의 구간만 있기에 삭제 할 수 없음!");
        }
    }

    public List<Section> dirtyChecking(final Sections sections) {
        List<Section> changedSections = new ArrayList<>();
        for (Section section : sections.sections) {
            if (!this.sections.contains(section)) {
                changedSections.add(section);
            }
        }
        return changedSections;
    }

    public boolean existSection(final Station upStation, final Station downStation) {
        List<Station> stations = stations();
        List<Station> targetStation = Arrays.asList(upStation, downStation);

        return stations.containsAll(targetStation);
    }

    public boolean noContainStation(final Station upStation, final Station downStation) {
        List<Station> stations = stations();

        return !stations.contains(upStation) && !stations.contains(downStation);
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

    private void deleteDownwardEndPointStation(final Station station) {
        Section findSection = findByDownStationSection(station);
        this.sections.remove(findSection);
    }

    private void deleteUpwardEndPointStation(final Station station) {
        Section findSection = findByUpStationSection(station);
        this.sections.remove(findSection);
    }

    private void validateDistance(final int baseDistance, final int targetDistance) {
        if (baseDistance <= targetDistance) {
            throw new InvalidInputException("기존 역 사이 길이보다 크거나 같을 수 없습니다");
        }
    }

    private Section tailSection() {
        for (Section source : sections) {
            if (tailMatchesCount(source) == 0) {
                return source;
            }
        }
        throw new InvalidInputException("구간이 제대로 등록되어있지 않음!");
    }

    private Section headSection() {
        for (Section source : sections) {
            if (headMatchesCount(source) == 0) {
                return source;
            }
        }
        throw new InvalidInputException("구간이 제대로 등록되어있지 않음!");
    }

    private int tailMatchesCount(final Section section) {
        Long tailStationId = section.downStation().id();
        int checkCount = 0;
        for (Section target : sections) {
            if (section.equals(target)) {
                continue;
            }
            if (tailStationId.equals(target.upStation().id()) || tailStationId.equals(target.downStation().id())) {
                checkCount++;
            }
        }
        return checkCount;
    }

    private int headMatchesCount(final Section section) {
        Long headStationId = section.upStation().id();
        int checkCount = 0;
        for (Section target : sections) {
            if (section.equals(target)) {
                continue;
            }
            if (headStationId.equals(target.upStation().id()) || headStationId.equals(target.downStation().id())) {
                checkCount++;
            }
        }
        return checkCount;
    }

    private Section findByUpStationSection(final Station targetUpStation) {
        for (Section section : sections) {
            if (section.sameUpStation(targetUpStation)) {
                return section;
            }
        }
        return null;
    }

    private Section findByDownStationSection(final Station targetDownStation) {
        for (Section section : sections) {
            if (section.sameDownStation(targetDownStation)) {
                return section;
            }
        }
        return null;
    }
}
