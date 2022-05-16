package wooteco.subway.service;

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

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    @Transactional
    public void save(SectionRequest sectionRequest, Long lineId) {
        Line line = findByLineId(lineId);
        Sections sections = new Sections(sectionDao.findAllByLineId(line.getId()));
        Sections updateSections = sections.update(toSection(sectionRequest, line));
        updateSectionsByLineId(line.getId(), updateSections);
    }

    private Section toSection(SectionRequest sectionRequest, Line line) {
        Station upStation = findByStationId(sectionRequest.getUpStationId());
        Station downStation = findByStationId(sectionRequest.getDownStationId());
        return new Section(upStation, downStation, line, sectionRequest.getDistance());
    }

    private Line findByLineId(Long lineId) {
        return lineDao.findById(lineId).orElseThrow(() -> new NotFoundException(lineId + "에 해당하는 노선을 찾을 수 없습니다."));
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        Sections updateSections = sections.deleteByStation(findByStationId(stationId));
        updateSectionsByLineId(lineId, updateSections);
    }

    private void updateSectionsByLineId(Long lineId, Sections updateSections) {
        sectionDao.deleteByLineId(lineId);
        sectionDao.saveAll(updateSections.value());
    }

    private Station findByStationId(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new NotFoundException(stationId + "에 해당하는 지하철 역을 찾을 수 없습니다."));
    }
}
