package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {
    private List<Section> sections;

    public Sections(Station upStation, Station downStation, int distance) {
        final Section section = Section.createWithoutId(upStation, downStation, distance);
        this.sections = new ArrayList<>() {
            {
                add(section);
            }
        };
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section section) {
        validateAlreadyRegistered(section);
        if (belongsToUpLine(section.getUpStation())) {
            updateSectionWithUpBranch(section);
            return;
        }
        if (belongsToDownLine(section.getDownStation())) {
            updateSectionWithDownBranch(section);
            return;
        }
        if (belongsToUpLine(section.getDownStation())) {
            updateSectionWithUpTerminal(section);
            return;
        }
        if (belongsToDownLine(section.getUpStation())) {
            updateSectionWithDownTerminal(section);
            return;
        }
        throw new IllegalArgumentException("상행역과 하행역 둘 다 노선에 포함되어 있지 않아 등록이 불가합니다.");
    }

    private void validateAlreadyRegistered(Section section) {
        if (isAllRegistered(section)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있어 등록이 불가능합니다.");
        }
    }

    private void updateSectionWithUpBranch(Section section) {
        final Section upLineSection = getUpLineSection(section.getUpStation());
        validateDistance(section, upLineSection);
        sections.remove(upLineSection);
        sections.add(section);
        sections.add(Section.createWithoutId(section.getDownStation(), upLineSection.getDownStation(),
                upLineSection.getDistance() - section.getDistance()));
    }

    private void updateSectionWithDownBranch(Section section) {
        final Section downLineSection = getDownLineSection(section.getDownStation());
        validateDistance(section, downLineSection);
        sections.remove(downLineSection);
        sections.add(Section.createWithoutId(downLineSection.getUpStation(), section.getUpStation(), downLineSection.getDistance() - section
                .getDistance()));
        sections.add(section);
    }

    private void updateSectionWithUpTerminal(Section section) {
        final Section upTerminalSection = getUpTerminalSection(section.getDownStation());
        sections.remove(upTerminalSection);
        sections.add(section);
        sections.add(upTerminalSection);
    }

    private void updateSectionWithDownTerminal(Section section) {
        sections.add(section);
    }

    private Section getUpTerminalSection(Station downStation) {
        return sections.stream()
                .filter(it -> it.isEqualToUpStation(downStation))
                .findFirst()
                .orElseThrow();
    }

    private Section getDownLineSection(Station downStation) {
        return sections.stream()
                .filter(it -> it.isEqualToDownStation(downStation))
                .findFirst()
                .orElseThrow();
    }

    private Section getUpLineSection(Station upStation) {
        return sections.stream()
                .filter(it -> it.isEqualToUpStation(upStation))
                .findFirst()
                .orElseThrow();
    }

    private boolean isAllRegistered(Section section) {
        return belongsToUpLine(section.getUpStation()) &&
                belongsToDownLine(section.getDownStation());
    }

    private boolean belongsToUpLine(Station upStation) {
        return sections.stream()
                .anyMatch(section -> section.isEqualToUpStation(upStation));
    }

    private boolean belongsToDownLine(Station downStation) {
        return sections.stream()
                .anyMatch(it -> it.getDownStation().equals(downStation));
    }

    private void validateDistance(Section section, Section section1) {
        if (section1.getDistance() <= section.getDistance()) {
            throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같으면 등록이 불가합니다.");
        }
    }

    public void deleteSection(Station station) {
        validateFinalSection();
        if (isUpLineTerminalStation(station)) {
            removeSection(getUpLineSection(station));
            return;
        }
        if (isDownLineTerminalStation(station)) {
            removeSection(getDownLineSection(station));
            return;
        }
        if (isNotTerminalStation(station)) {
            removeSectionOnMiddleLine(station);
        }
    }

    private void validateFinalSection() {
        if (sections.size() <= 1) {
            throw new IllegalArgumentException("마지막 구간은 제거할 수 없습니다.");
        }
    }

    private void removeSection(Section section) {
        sections.remove(section);
    }

    private void removeSectionOnMiddleLine(Station station) {
        final Section section = getUpLineSection(station);
        final Section upSection = getDownLineSection(station);
        sections.remove(section);
        sections.remove(upSection);
        final Section editedSection = Section.createWithoutId(upSection.getUpStation(), section.getDownStation(),
                upSection.getDistance() + section
                        .getDistance());
        sections.add(editedSection);
    }

    private boolean isUpLineTerminalStation(Station station) {
        return belongsToUpLine(station) && notBelongsToDownLine(station);
    }

    private boolean isDownLineTerminalStation(Station station) {
        return belongsToDownLine(station) &&
                notBelongsToUpLine(station);
    }

    private boolean notBelongsToUpLine(Station station) {
        return sections.stream()
                .noneMatch(section -> section.isEqualToUpStation(station));
    }

    private boolean isNotTerminalStation(Station station) {
        return belongsToDownLine(station) &&
                belongsToUpLine(station);
    }

    private boolean notBelongsToDownLine(Station station) {
        return sections.stream()
                .noneMatch(section -> section.isEqualToDownStation(station));
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return sections;
    }
}
