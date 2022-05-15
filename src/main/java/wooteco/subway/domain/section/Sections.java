package wooteco.subway.domain.section;

import java.util.List;

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
        return sectionLinks.getAllStationId();
    }

    public Section getMatchedSection(Section section) {
        validateExistMatchedSection(section);
        if (sectionLinks.isExistUpStation(section.getUpStationId())) {
            return getSameUpStationSection(section.getUpStationId());
        }
        return getSameDownStationSection(section.getDownStationId());
    }

    private void validateExistMatchedSection(Section section) {
        if (sectionLinks.isNotExistMatchedStation(section)) {
            throw new IllegalArgumentException(NOT_EXIST_SAME_SECTION);
        }
    }

    private Section getSameUpStationSection(Long id) {
        return sections.stream()
            .filter(it -> it.isSameAsUpStation(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_SAME_SECTION));
    }

    private Section getSameDownStationSection(Long id) {
        return sections.stream()
            .filter(it -> it.isSameAsDownStation(id))
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

    public void validateDeletable(Long stationId) {
        if (sections.size() == MIN_SIZE) {
            throw new IllegalStateException(MIN_SECTION_SIZE_EXCEPTION);
        }
        if (sectionLinks.isNotExistStation(stationId)) {
            throw new IllegalArgumentException(NOT_EXIST_STATION);
        }
    }

    public Section createCombineSection(Long stationId) {
        Section upSection = getSameUpStationSection(stationId);
        Section downSection = getSameDownStationSection(stationId);
        return upSection.createCombinedSection(downSection);
    }
}
