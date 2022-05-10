package wooteco.subway.domain;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import wooteco.subway.utils.exception.SectionCreateException;

public class Sections {

    private static final String SECTION_ALREADY_EXIST_MESSAGE = "이미 존재하는 구간입니다.";
    private static final String SECTION_NOT_CONNECT_MESSAGE = "구간이 연결되지 않습니다";
    private static final String SECTION_MUST_SHORTER_MESSAGE = "기존의 구간보다 긴 구간은 넣을 수 없습니다.";
    private static final String SECTION_CAN_NOT_DELETE_MESSAGE = "더이상 구간을 삭제할 수 없습니다.";
    private static final int MIN_SIZE = 1;


    private List<Section> values;

    public Sections(final List<Section> values) {
        this.values = new ArrayList<>(values);
    }

    public void add(final Section section) {
        validateDuplicateSection(section);
        validateSectionConnect(section);
        validateExistSection(section.getUpStation(), section.getDownStation());
        cutInSection(section);
        values.add(section);
    }

    private void validateExistSection(final Station upStation, final Station downStation) {
        Map<Station, Station> upToDownStations = values.stream()
                .collect(toMap(Section::getUpStation, Section::getDownStation));
        if (isSectionConnected(upToDownStations, upStation, downStation)
                || isSectionConnected(upToDownStations, downStation, upStation)) {
            throw new SectionCreateException(SECTION_ALREADY_EXIST_MESSAGE);
        }
    }

    private boolean isSectionConnected(final Map<Station, Station> upToDownStations,
                                       final Station upStation,
                                       final Station downStation) {
        return upToDownStations.containsKey(upStation) && upToDownStations.containsValue(downStation);
    }

    private void validateDuplicateSection(final Section section) {
        values.stream()
                .filter(value -> value.isSameSection(section))
                .findAny()
                .ifPresent(value -> {
                    throw new SectionCreateException(SECTION_ALREADY_EXIST_MESSAGE);
                });
    }

    private void validateSectionConnect(final Section section) {
        values.stream()
                .filter(value -> value.haveStation(section.getUpStation(), section.getDownStation()))
                .findAny()
                .orElseThrow(() -> new SectionCreateException(SECTION_NOT_CONNECT_MESSAGE));
    }

    private void cutInSection(final Section section) {
        Optional<Section> foundExistSectionPoint = values.stream()
                .filter(value -> value.isSameUpStation(value.getUpStation())
                        || value.isSameDownStation(value.getDownStation()))
                .findAny();
        if (foundExistSectionPoint.isPresent()) {
            Section foundSection = foundExistSectionPoint.get();
            validateCutInDistance(section, foundSection);
            updateCutInSection(section, foundSection);
        }
    }

    private void updateCutInSection(final Section section, final Section foundSection) {
        if (foundSection.isSameUpStation(section.getUpStation())) {
            foundSection.updateStations(section.getDownStation(), foundSection.getDownStation());
        }
        foundSection.updateStations(foundSection.getUpStation(), section.getUpStation());
        foundSection.subtractDistance(section.getDistance());
    }

    private void validateCutInDistance(final Section section, final Section foundSection) {
        if (!foundSection.isLongerThan(section.getDistance())) {
            throw new SectionCreateException(SECTION_MUST_SHORTER_MESSAGE);
        }
    }

    public Optional<Section> pickUpdate(List<Section> sections) {
        for (Section section : sections) {
            Optional<Section> updateSection = values.stream()
                    .filter(value -> value.isUpdate(section))
                    .findFirst();
            if (updateSection.isPresent()) {
                return updateSection;
            }
        }
        return Optional.empty();
    }

    public List<Section> delete(final Station station) {
        validateDelete();
        List<Section> sections = values.stream()
                .filter(value -> value.isSameUpStation(station)
                        || value.isSameDownStation(station))
                .collect(toList());

        for (Section section : sections) {
            values.remove(section);
        }
        return sections;
    }

    private void validateDelete() {
        if (values.size() <= MIN_SIZE) {
            throw new SectionCreateException(SECTION_CAN_NOT_DELETE_MESSAGE);
        }
    }

    public List<Section> getValues() {
        return List.copyOf(values);
    }
}
