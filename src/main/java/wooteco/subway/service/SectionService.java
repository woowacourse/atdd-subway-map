package wooteco.subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.NotFoundException;

@Service
public class SectionService {

    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private LineDao lineDao;

    @Transactional
    public void save(SectionRequest sectionRequest, Long lineId) {
        Line line = findByLineId(lineId);
        Station upStation = findByStationId(sectionRequest.getUpStationId());
        Station downStation = findByStationId(sectionRequest.getDownStationId());
        Sections sections = new Sections(sectionDao.findAllByLineId(line.getId()));
        Section section = new Section(upStation.getId(), downStation.getId(), lineId, sectionRequest.getDistance());
        Sections updateSections = sections.update(section);

        sectionDao.deleteByLineId(line.getId());
        sectionDao.saveAll(updateSections.value());
    }

    private Line findByLineId(Long lineId) {
        return lineDao.findById(lineId).orElseThrow(() -> new NotFoundException("노선을 찾을 수 없습니다."));
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        Sections updateSections = sections.deleteByStation(findByStationId(stationId));
        sectionDao.deleteByLineId(lineId);
        sectionDao.saveAll(updateSections.value());
    }

    private Station findByStationId(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new NotFoundException(stationId + "에 해당하는 지하철 역을 찾을 수 없습니다."));
    }
}
