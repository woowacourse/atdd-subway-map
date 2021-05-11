package wooteco.subway.section;

import wooteco.subway.exception.SubWayException;
import wooteco.subway.section.dto.SectionRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {
    private List<Section> sections;
    private List<Long> upStationIds;
    private List<Long> downStationIds;

    public Sections(List<Section> sections) {
        this.sections = sections;
        this.upStationIds = sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
        this.downStationIds = sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());
    }

    public void validateSavableSection(SectionRequest sectionReq) {
        long matchCount = Stream.concat(upStationIds.stream(), downStationIds.stream())
                .filter(id -> id.equals(sectionReq.getUpStationId()) || id.equals(sectionReq.getDownStationId()))
                .count();

        if (matchCount == 2 || matchCount == 0) {
            throw new SubWayException("등록 불가능한 구간입니다.");
        }
    }

    public List<Section> toList() {
        return new ArrayList<>(sections);
    }
}
