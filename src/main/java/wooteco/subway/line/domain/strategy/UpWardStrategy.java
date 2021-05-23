package wooteco.subway.line.domain.strategy;

import wooteco.subway.line.dao.SectionDao;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;

import java.util.List;

public class UpWardStrategy implements Strategy {
    private Section getSection(List<Section> sections, Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStation().isSame(stationId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("중복되는 역이 없습니다!!"));
    }

    @Override
    public Section selectedSection(Sections sections, Section newSection) {
        Long stationId = newSection.getUpStation().getId();
        return selectedSection(sections.getSections(), stationId);
    }

    @Override
    public Section selectedSection(List<Section> sections, Long stationId) {
        return getSection(sections, stationId);
    }

    @Override
    public void updateStation(SectionDao sectionDao, Section selectedSection, Section newSection) {
        Long lineId = newSection.getLine().getId();
        Long upStationId = newSection.getUpStation().getId();
        Long downStationId = newSection.getDownStation().getId();
        int distance = newSection.getDistance();

        sectionDao.updateUpStation(lineId, upStationId, downStationId, selectedSection.getDistance() - distance);
    }
}
