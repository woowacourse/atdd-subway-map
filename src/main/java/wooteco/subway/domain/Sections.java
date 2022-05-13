package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private static final String NOT_EXIST_SAME_SECTION = "일치하는 구간이 없습니다.";
    private static final String MIN_SECTION_SIZE_EXCEPTION = "구간이 하나 남아서 삭제 할 수 없음";
    private static final String ALL_SECTION_STATIONS_NOT_EXIST = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.";
    private static final String ALREADY_EXIST_SECTION_STATIONS = "상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.";
    private static final String NOT_EXIST_STATION = "역이 존재하지 않습니다.";
    private static final int MIN_SIZE = 1;

    private final List<Section> sections;
    private final SectionLinks sectionLinks;

    public Sections(List<Section> sections) {
        this.sections = sections;
        this.sectionLinks = SectionLinks.from(sections);
    }

    public List<Long> getAllStationId() {
        List<Long> stationIdS = new ArrayList<>();
        sections.forEach(section -> stationIdS.add(section.getUpStationId()));
        sections.forEach(section -> stationIdS.add(section.getDownStationId()));
        return stationIdS.stream()
            .distinct()
            .collect(Collectors.toList());
    }

    private Section getSameUpStationSection(Long id) {
        return sections.stream()
            .filter(it -> it.isSameUpStation(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_SAME_SECTION));
    }

    private Section getSameDownStationSection(Long id) {
        return sections.stream()
            .filter(it -> it.isSameDownStation(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_SAME_SECTION));
    }

    public void validateAddable(Section section) {
        if (sectionLinks.isNotExistMatchedStation(section)) {
            throw new IllegalStateException(ALL_SECTION_STATIONS_NOT_EXIST);
        }
        if (sectionLinks.isAllMatchedStation(section)) {
            throw new IllegalStateException(ALREADY_EXIST_SECTION_STATIONS);
        }
    }

    public boolean isEndSection(Section section) {
        return sectionLinks.isEndSection(section);
    }

    public boolean isEndStation(Long id) {
        return sectionLinks.isEndStation(id);
    }

    public Section searchMatchedSection(Section section) {
        if (isExistUpStation(section.getUpStationId())) {
            return getSameUpStationSection(section.getUpStationId());
        }
        if (isExistDownStation(section.getDownStationId())) {
            return getSameDownStationSection(section.getDownStationId());
        }
        throw new IllegalArgumentException(NOT_EXIST_SAME_SECTION);
    }

    private boolean isExistUpStation(Long upStationId) {
        return sectionLinks.isExistUpStation(upStationId);
    }

    private boolean isExistDownStation(Long downStationId) {
        return sectionLinks.isExistDownStation(downStationId);
    }

    private boolean isNotExistStation(Long stationId) {
        return sectionLinks.isNotExistStation(stationId);
    }

    public void validateDeletable(Long stationId) {
        if (sections.size() == MIN_SIZE) {
            throw new IllegalStateException(MIN_SECTION_SIZE_EXCEPTION);
        }
        if (isNotExistStation(stationId)) {
            throw new IllegalArgumentException(NOT_EXIST_STATION);
        }
    }

    public Section createCombineSection(Long stationId) {
        Section upSection = getSameUpStationSection(stationId);
        Section downSection = getSameDownStationSection(stationId);
        return upSection.createCombineSection(downSection);
    }

    public SectionLinks getSectionLinks() {
        return sectionLinks;
    }
}
