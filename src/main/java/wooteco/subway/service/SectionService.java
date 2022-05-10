package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

import java.util.List;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(final Long lineId, final SectionRequest sectionRequest) {
        Sections sections = getSections(lineId);

        sections.add(new Section(
                lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance())
        );

        sectionDao.save(
                lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance()
        );
    }

    public void deleteById(final Long lineId, final Long stationId) {
        sectionDao.deleteById(lineId, stationId);
    }

    public Sections getSections(final Long lineId) {
        List<Section> sections = sectionDao.getSectionByLineId(lineId);
        return new Sections(sections);
    }
}
