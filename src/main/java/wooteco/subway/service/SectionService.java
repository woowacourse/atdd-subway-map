package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }


    public void save(Long lineId, SectionRequest sectionReq) {
        if (!sectionDao.existByLineId(lineId)) {
            sectionDao.save(createSection(lineId, sectionReq, 1L));
            return;
        }

        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));

        long upStationId = sectionReq.getUpStationId();
        long downStationId = sectionReq.getDownStationId();
        int distance = sectionReq.getDistance();

        sections.validateSection(upStationId, downStationId, distance);

        Section section = sections.find겹치는SectionId(upStationId, downStationId, distance);
        if (section.getId() != null) {
            sectionDao.deleteById(section.getId());
            sectionDao.save(section);
        }
        Sections sectionsss = new Sections(sectionDao.findAllByLineId(lineId));
        sectionDao.updateLineOrder(lineId, section.getLineOrder());
        sectionDao.save(createSection(lineId, sectionReq, section.getLineOrder()));
        Sections sectionss = new Sections(sectionDao.findAllByLineId(lineId));
    }

    private Section createSection(Long lineId, SectionRequest sectionReq, Long lineOrder) {
        return new Section(
                null,
                lineId,
                sectionReq.getUpStationId(),
                sectionReq.getDownStationId(),
                sectionReq.getDistance(),
                lineOrder
        );
    }

    public List<Long> findAllStationByLineId(long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        return sections.getStationsId();
    }
}
