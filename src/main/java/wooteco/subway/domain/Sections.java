package wooteco.subway.domain;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.utils.exception.SectionCreateException;
import wooteco.subway.utils.exception.SectionDeleteException;
import wooteco.subway.utils.exception.SubwayException;

public class Sections {

    public static final int NEED_MERGE_SIZE = 2;

    private static final String SECTION_ALREADY_EXIST_MESSAGE = "이미 존재하는 구간입니다.";
    private static final String SECTION_NOT_CONNECT_MESSAGE = "구간이 연결되지 않습니다";
    private static final String SECTION_MUST_SHORTER_MESSAGE = "기존의 구간보다 긴 구간은 넣을 수 없습니다.";
    private static final int MIN_SIZE = 1;

    private final List<Section> values;

    public Sections(final List<Section> values) {
        this.values = new ArrayList<>(values);
    }

    public void add(final Section section) {
        validateSectionCreate(section);
        cutInSection(section);
        values.add(section);
    }

    private void validateSectionCreate(final Section section) {
        validateDuplicateSection(section);
        validateSectionConnect(section);
        validateExistSection(section);
    }

    private void validateExistSection(final Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        if (isSectionConnected(upStation, downStation) || isSectionConnected(downStation, upStation)) {
            throw new SectionCreateException(SECTION_ALREADY_EXIST_MESSAGE);
        }
    }

    private boolean isSectionConnected(final Station upStation, final Station downStation) {
        return getUpStations().contains(upStation) && getDownStations().contains(downStation);
    }

    private List<Station> getUpStations() {
        return values.stream()
                .map(Section::getUpStation)
                .collect(toList());
    }

    private List<Station> getDownStations() {
        return values.stream()
                .map(Section::getDownStation)
                .collect(toList());
    }

    private void validateDuplicateSection(final Section section) {
        boolean exist = values.stream()
                .anyMatch(value -> value.isSameSection(section));
        if (exist) {
            throw new SectionCreateException(SECTION_NOT_CONNECT_MESSAGE);
        }

    }

    private void validateSectionConnect(final Section section) {
        boolean exist = values.stream()
                .anyMatch(value -> value.haveStation(section));
        if (!exist) {
            throw new SectionCreateException(SECTION_NOT_CONNECT_MESSAGE);
        }
    }

    private void cutInSection(final Section section) {
        values.stream()
                .filter(value -> value.isSameUpStation(section.getUpStation())
                        || value.isSameDownStation(section.getDownStation()))
                .findAny()
                .ifPresent(foundSection -> updateCutInSection(section, foundSection));
    }

    private void updateCutInSection(final Section section, final Section foundSection) {
        validateCutInDistance(section, foundSection);
        if (foundSection.isSameUpStation(section.getUpStation())) {
            updateStationAndDistance(foundSection,
                    section.getDownStation(),
                    foundSection.getDownStation(),
                    section.getDistance());
            return;
        }
        updateStationAndDistance(foundSection,
                foundSection.getUpStation(),
                section.getUpStation(),
                section.getDistance());
    }

    private void validateCutInDistance(Section section, Section foundSection) {
        if (!foundSection.isLongerThan(section.getDistance())) {
            throw new SectionCreateException(SECTION_MUST_SHORTER_MESSAGE);
        }
    }

    private void updateStationAndDistance(final Section section,
                                          final Station upStation,
                                          final Station downStation,
                                          final int distance) {
        section.updateStations(upStation, downStation);
        section.subtractDistance(distance);
    }

    public Optional<Section> pickUpdate(final List<Section> sections) {
        return values.stream()
                .filter(value -> findUpdateSection(sections, value))
                .findFirst();
    }

    private boolean findUpdateSection(final List<Section> sections, final Section section) {
        return sections.stream()
                .anyMatch(value -> value.isUpdate(section));
    }

    public List<Section> delete(final Station station) {
        validateDelete();
        List<Section> sections = values.stream()
                .filter(value -> value.isSameUpStation(station)
                        || value.isSameDownStation(station))
                .collect(toList());
        values.removeAll(sections);
        if (isSectionNeedMerge(sections)) {
            values.add(sections.get(0).merge(sections.get(1)));
        }
        return sections;
    }

    private boolean isSectionNeedMerge(final List<Section> sections) {
        return sections.size() == NEED_MERGE_SIZE;
    }

    private void validateDelete() {
        if (values.size() <= MIN_SIZE) {
            throw new SectionDeleteException();
        }
    }

    public List<StationResponse> sortSections() {
        List<StationResponse> stationResponses = new ArrayList<>();
        Station firstStation = findFirstStation();
        stationResponses.add(new StationResponse(firstStation));
        while (nextStation(firstStation).isPresent()) {
            firstStation = nextStation(firstStation).get();
            stationResponses.add(new StationResponse(firstStation));
        }
        return stationResponses;
    }

    private Station findFirstStation() {
        List<Station> downStations = getDownStations();
        List<Station> upStations = getUpStations();

        return upStations.stream()
                .filter(station -> !downStations.contains(station))
                .findFirst()
                .orElseThrow(() -> new SubwayException("[ERROR] 첫번째 구간을 찾을 수 없습니다."));
    }

    private Optional<Station> nextStation(final Station station) {
        return values.stream()
                .filter(value -> value.isSameUpStation(station))
                .map(Section::getDownStation)
                .findFirst();
    }

    public List<Section> getValues() {
        return List.copyOf(values);
    }

    @Override
    public String toString() {
        return "Sections{" +
                "values=" + values +
                '}';
    }
}
