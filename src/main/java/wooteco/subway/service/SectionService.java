package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.ModifyResult;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public ModifyResult save(SectionRequest sectionRequest, Long lineId) {
        Section section = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        Sections sections = new Sections(sectionDao.findAllByLineId(section.getLineId()));
        ModifyResult modifyResult = sections.add(section);
        for (Section eachSection : modifyResult.getSaveResult()) {
            sectionDao.save(eachSection, section.getLineId());
        }
        for (Section eachSection : modifyResult.getDeleteResult()) {
            sectionDao.deleteByLineIdAndStationIds(section.getLineId(), eachSection.getUpStationId(), eachSection.getDownStationId());
        }
        return modifyResult;
    }

    public ModifyResult delete(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        ModifyResult modifyResult = sections.delete(lineId, stationId);
        for (Section eachSection : modifyResult.getSaveResult()) {
            sectionDao.save(eachSection, lineId);
        }
        for (Section eachSection : modifyResult.getDeleteResult()) {
            sectionDao.deleteByLineIdAndStationIds(lineId, eachSection.getUpStationId(), eachSection.getDownStationId());
        }
        return modifyResult;
    }
}
