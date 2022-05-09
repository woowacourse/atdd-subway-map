package wooteco.subway.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import wooteco.subway.utils.exception.SectionCreateException;

public class Sections {

    private static final String SECTION_ALREADY_EXIST_MESSAGE = "이미 존재하는 구간입니다.";
    private static final String SECTION_NOT_CONNECT_MESSAGE = "구간이 연결되지 않습니다";

    private List<Section> values;

    public Sections(final List<Section> values) {
        this.values = values;
    }

    public void add(final Section section) {
        validateDuplicateSection(section);
        validateSectionConnect(section);
        validateExistSection(section.getUpStation(), section.getDownStation());
        values.add(section);
    }

    private void validateExistSection(final Station upStation, final Station downStation) {
        Map<Station, Station> upToDownStations = values.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
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
                .filter(value -> value.equals(section))
                .findAny()
                .ifPresent(value -> {
                    throw new SectionCreateException(SECTION_ALREADY_EXIST_MESSAGE);
                });
    }

    private void validateSectionConnect(Section section) {
        values.stream()
                .filter(value -> value.haveStation(section.getUpStation(), section.getDownStation()))
                .findAny()
                .orElseThrow(() -> new SectionCreateException(SECTION_NOT_CONNECT_MESSAGE));
    }

    public List<Section> getValues() {
        return List.copyOf(values);
    }
}
