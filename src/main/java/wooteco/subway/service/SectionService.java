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
        Sections sections = new Sections(sectionDao.getSectionByLineId(lineId));
        sections.remove(stationId);

        List<Section> stationIds = sections.getSectionContainsStation(stationId);
        if (stationIds.size() == 1) {
            sectionDao.deleteById(lineId, stationId);
            return;
        }

        Section upSection = sections.getUpSection(stationId, stationIds);
        Section downSection = sections.getDownSection(stationId, stationIds);
        sectionDao.deleteById(lineId, stationId);
        sectionDao.save(
                lineId,
                downSection.getUpStationId(),
                upSection.getDownStationId(),
                downSection.getDistance() + upSection.getDistance()
        );
    }

    public Sections getSections(final Long lineId) {
        List<Section> sections = sectionDao.getSectionByLineId(lineId);
        return new Sections(sections);
    }
}
