package wooteco.subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.NotFoundException;

@Service
public class SectionService {

    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private StationDao stationDao;

    public void save(SectionRequest sectionRequest, Long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        Section section = new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), lineId,
                sectionRequest.getDistance());
        Sections updateSections = sections.update(section);
        sectionDao.deleteByLineId(lineId);
        sectionDao.saveAll(updateSections.value());
    }

    public void delete(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        Sections updateSections = sections.deleteByStation(stationDao.findById(stationId)
                .orElseThrow(() -> new NotFoundException(stationId + "에 해당하는 지하철 역을 찾을 수 없습니다.")));
        sectionDao.deleteByLineId(lineId);
        sectionDao.saveAll(updateSections.value());
    }
}
