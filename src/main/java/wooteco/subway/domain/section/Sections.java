package wooteco.subway.domain.section;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.NotAddableSectionException;
import wooteco.subway.exception.SectionExistException;

public class Sections {

    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return sections.stream()
            .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
            .collect(Collectors.toList());
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        checkExistSection(section);
        checkExistStation(section);

        if (!isFirstOrLastSection(section)) {
            sections.add(section);
            return;
        }

        addUpOrDown(section);
    }

    public boolean isFirstOrLastSection(Section section) {
        boolean isFirst = sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList())
            .contains(section.getUpStation());

        boolean isLast = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList())
            .contains(section.getDownStation());

        return isFirst || isLast;
    }


    private void addUpOrDown(Section section) {
        this.sections.stream()
            .filter(sct -> sct.getUpStation().equals(section.getUpStation()))
            .findFirst()
            .ifPresent(savedSection -> addUpAndMiddleSection(section, savedSection));

        this.sections.stream()
            .filter(sct -> sct.getDownStation().equals(section.getDownStation()))
            .findFirst()
            .ifPresent(savedSection -> addDownAndMiddleSection(section, savedSection));
    }

    private void addUpAndMiddleSection(Section section, Section savedSection) {
        checkDistance(section, savedSection);
        addNewSection(section);

        this.sections.add(new Section(
            section.getUpStation(),
            savedSection.getUpStation(),
            savedSection.getDistance() - section.getDistance()
        ));
    }

    private void addDownAndMiddleSection(Section section, Section savedSection) {
        checkDistance(section, savedSection);
        addNewSection(section);

        this.sections.add(new Section(
            section.getDownStation(),
            savedSection.getDownStation(),
            savedSection.getDistance() - section.getDistance()
        ));
    }

    private void checkDistance(Section section, Section savedSection) {
        if (section.getDistance() >= savedSection.getDistance()) {
            throw new NotAddableSectionException();
        }
        this.sections.remove(savedSection);
    }

    private void addNewSection(Section section) {
        this.sections.add(new Section(
            section.getUpStation(),
            section.getDownStation(),
            section.getDistance()
        ));
    }

    private void checkExistSection(Section section) {
        if (sections.contains(section)) {
            throw new SectionExistException();
        }
    }


    private void checkExistStation(Section section) {
        List<Station> stations = getStations();
        if (!stations.contains(section.getUpStation()) &&
            !stations.contains(section.getDownStation())) {
            throw new NotAddableSectionException();
        }
    }
}
