package wooteco.subway.section.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import wooteco.subway.exception.DeleteSectionException;
import wooteco.subway.exception.DuplicatedSectionException;
import wooteco.subway.exception.NotContainStationsException;
import wooteco.subway.exception.NotExistStationException;
import wooteco.subway.exception.NotFoundTerminalStationException;
import wooteco.subway.station.domain.Station;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.sections = sortedSections(sections);
    }

    private List<Section> sortedSections(List<Section> sections) {
        List<Section> sortedSection = new ArrayList<>();
        Station upStation = upTerminalSection(sections).getUpStation();

        for (int i = 0; i < sections.size(); i++) {
            for (Section section : sections) {
                if (section.getUpStation().equals(upStation)) {
                    sortedSection.add(section);
                    upStation = section.getDownStation();
                }
            }
        }
        return sortedSection;
    }

    private Section upTerminalSection(List<Section> sections) {
        List<Long> upStationIds = new LinkedList<>();
        List<Long> downStationIds = new LinkedList<>();

        sections.forEach(section -> {
            upStationIds.add(section.getUpStationId());
            downStationIds.add(section.getDownStationId());
        });

        upStationIds.removeAll(downStationIds);
        Long upTerminalStationId = upStationIds.get(0);

        return sections.stream()
            .filter(section -> section.getUpStationId().equals(upTerminalStationId))
            .findFirst()
            .orElseThrow(NotFoundTerminalStationException::new);
    }

    public boolean isTerminalSection(Section section) {
        return sections.get(0).getUpStation().equals(section.getDownStation())
            || sections.get(sections.size() - 1).getDownStation().equals(section.getUpStation());
    }

    public boolean isTerminalStation(Station station) {
        return sections.get(0).getUpStation().equals(station)
            || sections.get(sections.size() - 1).getDownStation().equals(station);
    }

    public void validateAddable(Section section) {
        validateDuplicatedSection(section);
        validateContainStation(section);
    }

    public void validateDuplicatedSection(Section section) {
        if (sections.stream()
            .noneMatch((it -> section.equals(section)))) {
            throw new DuplicatedSectionException();
        }
    }

    public void validateContainStation(Section section) {
        List<Station> stations = sortedStations();

        if (!stations.contains(section.getUpStation())
            && !stations.contains(section.getDownStation())) {
            throw new NotContainStationsException();
        }
    }

    public void validateRemovable(Station station) {
        if (sections.size() == 1) {
            throw new DeleteSectionException();
        }

        for (Section section : sections) {
            if (section.getUpStation().equals(station)
                || section.getDownStation().equals(station)) {
                return;
            }
        }
        throw new NotExistStationException();
    }

    public Section createdSectionByAddSection(Section section) {
        for (Section currentSection : sections) {
            if (currentSection.isSameUpStation(section)) {
                return new Section(section.getLineId(), section.getDownStation(),
                    currentSection.getDownStation(), currentSection.minusDistance(section));
            }
            if (currentSection.isSameDownStation(section)) {
                return new Section(section.getLineId(), currentSection.getUpStation(),
                    section.getUpStation(), currentSection.minusDistance(section));
            }
        }
        throw new IllegalArgumentException();
    }

    public Section removedSectionByAddSection(Section section) {
        for (Section currentSection : sections) {
            if (currentSection.isSameUpStation(section) || currentSection
                .isSameDownStation(section)) {
                return currentSection;
            }
        }
        throw new IllegalArgumentException();
    }

    public Section createdSectionByRemoveStation(Long lineId, Station station) {
        for (int i = 0; i < sections.size(); i++) {
            Section currentSection = sections.get(i);
            if (currentSection.getUpStation().equals(station)) {
                Section prevSection = sections.get(i - 1);
                int newDistance = prevSection.getDistance() + currentSection.getDistance();
                return new Section(lineId, prevSection.getUpStation(),
                    currentSection.getDownStation(), newDistance);
            }
        }
        throw new IllegalArgumentException();
    }

    public List<Section> removedSectionsByRemoveStation(Station station) {
        List<Section> removedSections = new ArrayList<>();

        for (int i = 0; i < sections.size(); i++) {
            Section currentSection = sections.get(i);
            if (currentSection.getUpStation().equals(station)) {
                Section prevSection = sections.get(i - 1);
                removedSections.add(prevSection);
                removedSections.add(currentSection);
            }
        }
        return removedSections;
    }

    public Section removedTerminalSectionByRemoveStation(Station station) {
        Section upTerminalSection = sections.get(0);
        if (upTerminalSection.getUpStation().equals(station)) {
            return upTerminalSection;
        }

        Section downTerminalSection = sections.get(sections.size() - 1);
        if (downTerminalSection.getDownStation().equals(station)) {
            return downTerminalSection;
        }
        throw new IllegalArgumentException();
    }

    public List<Station> sortedStations() {
        List<Station> stations = new ArrayList<>();
        for (Section each : sections) {
            stations.add(each.getUpStation());
        }
        stations.add(sections.get(stations.size() - 1).getDownStation());
        return stations;
    }

}