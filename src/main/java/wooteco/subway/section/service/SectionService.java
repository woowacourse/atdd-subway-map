package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.Line;
import wooteco.subway.line.repository.LineDao;
import wooteco.subway.section.Section;
import wooteco.subway.section.Sections;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.dto.SectionResponse;
import wooteco.subway.section.repository.SectionDao;
import wooteco.subway.station.Station;
import wooteco.subway.station.service.StationService;

import java.util.List;

@Service
public class SectionService {
    private final StationService stationService;
    private final SectionDao sectionDao;
    private final LineDao lineDao;

    public SectionService(StationService stationService, SectionDao sectionDao, LineDao lineDao) {
        this.stationService = stationService;
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    @Transactional
    public void save(Line newLine, SectionRequest sectionReq) {
        stationService.validateExistStations(sectionReq.getUpStationId(), sectionReq.getDownStationId());
        sectionDao.save(sectionReq.toEntity(newLine));
    }

    @Transactional
    public SectionResponse appendSection(Long lineId, SectionRequest sectionReq) {
        stationService.validateExistStations(sectionReq.getUpStationId(), sectionReq.getDownStationId());
        Section newSection = sectionReq.toEntity(lineDao.findById(lineId));
        Sections sections =  new Sections(sectionDao.findAllByLineId(lineId), newSection);
        sections.addSection(lineDao.findById(lineId), newSection);

        sectionDao.deleteByLineId(lineDao.findById(lineId));
        sections.toSortedSections().forEach(sectionDao::save);
        return SectionResponse.from(newSection);
    }

    @Transactional(readOnly = true)
    public List<Long> findAllSectionsId(Long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        return sections.toSortedStationIds();
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        Station station = stationService.findBy(stationId);
        Line line = lineDao.findById(lineId);

        sections.removeSection(line, station);
        sectionDao.deleteByLineId(line);
        sections.toSortedSections().forEach(sectionDao::save);
    }
}
