package wooteco.subway.domain;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections implements Iterable<Section> {

    private static final String UNVALID_STATION_FOR_SECTION_ADD_EXCEPTION = "추가하려는 section의 역 중 하나는 기존 section에 포함되어 있어야 합니다.";
    private static final String UNABLE_TO_DELETE_SECTION_EXCEPTION = "해당 역을 삭제할 수 없습니다. 노선에 역은 최소 2개는 존재해야 합니다.";
    private static final int MINIMUM_SECTION_NUMBER = 1;

    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public SectionBuffer add(Section newSection) {
        SectionBuffer sectionBuffer = new SectionBuffer();
        validateStationsInSection(newSection);
        if (ableToLinkUpStation(newSection)) {
            return linkUpStation(newSection, sectionBuffer);
        }
        return linkDownStation(newSection, sectionBuffer);
    }

    private SectionBuffer linkUpStation(Section newSection, SectionBuffer sectionBuffer) {
        Optional<Section> dividedSection = sections.stream()
                .filter(it -> it.isSameUpStation(newSection))
                .findAny();

        return divideSection(newSection, dividedSection, sectionBuffer);
    }

    private SectionBuffer linkDownStation(Section newSection, SectionBuffer sectionBuffer) {
        Optional<Section> dividedSection = sections.stream()
                .filter(it -> it.isSameDownStation(newSection))
                .findAny();

        return divideSection(newSection, dividedSection, sectionBuffer);
    }

    private SectionBuffer divideSection(Section newSection, Optional<Section> dividedSection, SectionBuffer sectionBuffer) {
        if (dividedSection.isPresent()) {
            sectionBuffer.addToDeleteBuffer(dividedSection.get());
            sectionBuffer.addToAddBuffer(dividedSection.get().subtractSection(newSection));
        }
        sectionBuffer.addToAddBuffer(newSection);
        return sectionBuffer;
    }

    private boolean ableToLinkUpStation(Section newSection) {
        return sections.stream()
                .anyMatch(it -> it.isSameUpStation(newSection));
    }

    private void validateStationsInSection(Section section) {
        boolean downStationExist = isStationExist(section.getDownStation());
        boolean upStationExist = isStationExist(section.getUpStation());

        if (downStationExist == upStationExist) {
            throw new IllegalArgumentException(UNVALID_STATION_FOR_SECTION_ADD_EXCEPTION);
        }
    }

    private boolean isStationExist(Station station) {
        return sections.stream()
                .anyMatch(it -> it.hasStation(station));
    }

    public SectionBuffer delete(Station station) {
        SectionBuffer sectionBuffer = new SectionBuffer();
        validateAbleToDelete();

        List<Section> sectionsContainDeleteStation = getSectionsContainDeleteStation(station);
        sectionsContainDeleteStation.forEach(sectionBuffer::addToDeleteBuffer);
        mergeIfPossible(sectionsContainDeleteStation, sectionBuffer);
        return sectionBuffer;
    }

    private void mergeIfPossible(List<Section> sectionsContainDeleteStation, SectionBuffer sectionBuffer) {
        if (sectionsContainDeleteStation.size() == 2) {
            Section section1 = sectionsContainDeleteStation.get(0);
            Section section2 = sectionsContainDeleteStation.get(1);
            Section newSection = section1.merge(section2);
            sectionBuffer.addToAddBuffer(newSection);
        }
    }

    private List<Section> getSectionsContainDeleteStation(Station station) {
        return sections.stream()
                .filter(it -> it.hasStation(station))
                .collect(Collectors.toList());
    }

    private void validateAbleToDelete() {
        if (sections.size() == MINIMUM_SECTION_NUMBER) {
            throw new IllegalArgumentException(UNABLE_TO_DELETE_SECTION_EXCEPTION);
        }
    }

    @Override
    public Iterator<Section> iterator() {
        return sections.iterator();
    }
}
