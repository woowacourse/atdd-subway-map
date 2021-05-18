package wooteco.subway.line.domain.strategy;

import wooteco.subway.line.dao.SectionDao;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;

import java.util.List;

public interface Strategy {
    Section selectedSection(Sections sections, Section newSection);

    Section selectedSection(List<Section> sections, Long stationId);

    void updateStation(SectionDao sectionDao, Section selectedSection, Section newSection);
}
