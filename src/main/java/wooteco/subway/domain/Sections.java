package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Sections {
    private static final int CANNOT_DELETE_SECTION_MAX_SIZE = 1;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    private Sections() {
        this(new ArrayList<>());
    }

    public void add(Section section) {
        boolean isIncludeUpStation = hasSameUpStation(section) || hasSameDownStationWithOtherUpStation(section);
        boolean isIncludeDownStation = hasSameDownStation(section) || hasSameUpStationWithOtherDownStation(section);
        validateSection(isIncludeUpStation, isIncludeDownStation);
        if (isIncludeUpStation) {
            addWithSameUpStation(section);
            return;
        }
        addWithSameDownStation(section);
    }

    private void validateSection(boolean isIncludeUpStation, boolean isIncludeDownStation) {
        if (!isIncludeUpStation && !isIncludeDownStation) {
            throw new IllegalArgumentException("등록하려는 구간의 상행역과 하행역 둘 중 하나는 노선에 포함된 역이어야 합니다.");
        }
        if (isIncludeUpStation && isIncludeDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 존재합니다.");
        }
    }

    private void addWithSameUpStation(Section section) {
        if (hasSameUpStation(section)) {
            addSplitUpSection(section);
            return;
        }
        sections.add(section);
    }

    private void addSplitUpSection(Section addSection) {
        final Section existSection = sections.stream()
                .filter(it -> it.hasSameUpStation(addSection))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("구간 등록 중 상행역 불일치로 오류가 발생했습니다."));
        validateSectionDistance(addSection, existSection);
        sections.add(addSection);
        sections.add(existSection.splitSectionBySameUpStation(addSection));
        sections.remove(existSection);
    }

    private void addWithSameDownStation(Section section) {
        if (hasSameDownStation(section)) {
            addSplitDownSection(section);
            return;
        }
        sections.add(section);
    }

    private void addSplitDownSection(Section section) {
        final Section existSection = sections.stream()
                .filter(it -> it.hasSameDownStation(section))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("구간 등록 중 하행역 불일치로 오류가 발생했습니다."));
        validateSectionDistance(section, existSection);
        sections.add(section);
        sections.add(existSection.splitSectionBySameDownStation(section));
        sections.remove(existSection);
    }

    private void validateSectionDistance(Section section, Section existSection) {
        if (!existSection.isLongerThan(section)) {
            throw new IllegalArgumentException("존재하는 구간보다 긴 구간을 등록할 수 없습니다. 상행역, 하행역을 다시 설정해주세요.");
        }
    }

    private boolean hasSameUpStation(Section section) {
        return sections.stream()
                .anyMatch(it -> it.hasSameUpStation(section));
    }

    private boolean hasSameDownStation(Section section) {
        return sections.stream()
                .anyMatch(it -> it.hasSameDownStation(section));
    }

    private boolean hasSameUpStationWithOtherDownStation(Section section) {
        return sections.stream()
                .anyMatch(it -> it.hasSameUpStationWithOtherDownStation(section));
    }

    private boolean hasSameDownStationWithOtherUpStation(Section section) {
        return sections.stream()
                .anyMatch(it -> it.hasSameDownStationWithOtherUpStation(section));
    }

    public void delete(Station station) {
        boolean hasUpSection = hasUpSectionByStation(station);
        boolean hasDownSection = hasDownSectionByStation(station);
        checkDeleteSafety(hasUpSection, hasDownSection);
        if (hasUpSection && hasDownSection) {
            deleteAndMergeSections(station);
            return;
        }
        deleteFinalStation(station, hasUpSection);
    }

    private boolean hasUpSectionByStation(Station station) {
        return sections.stream()
                .anyMatch(it -> it.hasSameDownStationByStation(station));
    }

    private boolean hasDownSectionByStation(Station station) {
        return sections.stream()
                .anyMatch(it -> it.hasSameUpStationByStation(station));
    }

    private void checkDeleteSafety(boolean hasUpSection, boolean hasDownSection) {
        if (!hasUpSection && !hasDownSection) {
            throw new IllegalArgumentException("존재하지 않는 역입니다.");
        }
        if (sections.size() == CANNOT_DELETE_SECTION_MAX_SIZE) {
            throw new IllegalArgumentException("구간이 하나만 존재하는 노선입니다.");
        }
    }

    private void deleteAndMergeSections(Station station) {
        final Section upSection = findUpSectionByStation(station);
        final Section downSection = findDownSectionByStation(station);
        sections.add(upSection.mergeSectionByCut(downSection));
        sections.remove(upSection);
        sections.remove(downSection);
    }

    private void deleteFinalStation(Station station, boolean hasUpSection) {
        if (hasUpSection) {
            sections.remove(findUpSectionByStation(station));
            return;
        }
        sections.remove(findDownSectionByStation(station));
    }

    private Section findUpSectionByStation(Station station) {
        return sections.stream()
                .filter(it -> it.hasSameDownStationByStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("구간 삭제 중 상행역 연결 오류가 발생했습니다."));
    }

    private Section findDownSectionByStation(Station station) {
        return sections.stream()
                .filter(it -> it.hasSameUpStationByStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("구간 삭제 중 하행역 연결 오류가 발생했습니다."));
    }

    public boolean hasSection(Section section) {
        return sections.stream()
                .anyMatch(it -> it.isSameStations(section));
    }

    public List<Section> getSections() {
        return sections;
    }
}
