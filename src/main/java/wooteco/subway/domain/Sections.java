package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {
    private List<Section> sections;

    public Sections(Station upStation, Station downStation, int distance) {
        final Section section = new Section(upStation, downStation, distance);
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
            final Section upLineSection = getUpLineSection(section.getUpStation());
            validateDistance(section, upLineSection);
            sections.remove(upLineSection);
            sections.add(section);
            sections.add(new Section(section.getDownStation(), upLineSection.getDownStation(),
                    upLineSection.getDistance() - section.getDistance()));
            return;
        }

        if (belongsToDownLine(section.getDownStation())) {
            final Section section1 = getDownLineSection(section.getDownStation());
            validateDistance(section, section1);
            sections.remove(section1);
            sections.add(new Section(section1.getUpStation(), section.getUpStation(), section1.getDistance() - section
                    .getDistance()));
            sections.add(section);
            return;
        }

        if (belongsToUpLine(section.getDownStation())) {
            final Section section1 = getUpTerminalSection(section.getDownStation());
            sections.remove(section1);
            sections.add(section);
            sections.add(section1);
            return;
        }

        if (belongsToDownLine(section.getUpStation())) {
            sections.add(section);
            return;
        }

        throw new IllegalArgumentException("상행역과 하행역 둘 다 노선에 포함되어 있지 않아 등록이 불가합니다.");
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

    private void validateAlreadyRegistered(Section section) {
        if (isAllRegistered(section)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있어 등록이 불가능합니다.");
        }
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
        if (sections.size() <= 1) {
            throw new IllegalArgumentException("마지막 구간은 제거할 수 없습니다.");
        }

        if (isUpLineTerminalStation(station)) {
            final Section section = getUpLineSection(station);
            sections.remove(section);
            return;
        }

        if (isDownLineTerminalStation(station)) {
            final Section section = getDownLineSection(station);
            sections.remove(section);
            return;
        }

        if (isNotTerminalStation(station)) {
            final Section section = getUpLineSection(station);
            final Section upSection = getDownLineSection(station);
            sections.remove(section);
            sections.remove(upSection);
            final Section editedSection = new Section(upSection.getUpStation(), section.getDownStation(),
                    upSection.getDistance() + section
                            .getDistance());
            sections.add(editedSection);
        }
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
