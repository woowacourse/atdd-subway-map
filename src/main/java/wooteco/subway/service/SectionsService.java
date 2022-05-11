package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Service
public class SectionsService {
    private static final String ALREADY_IN_LINE_ERROR_MESSAGE = "지하철 노선에 해당 역이 등록되어있어 역을 삭제할 수 없습니다.";

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;
    private final CheckService checkService;

    public SectionsService(SectionDao sectionDao, StationDao stationDao, LineDao lineDao,
                           CheckService checkService) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.checkService = checkService;
    }

    @Transactional
    public Section save(Section section) {
        checkService.checkLineExist(section.getLineId());
        checkService.checkStationsExist(section);
        Sections sections = Sections.forSave(sectionDao.findAllByLineId(section.getLineId()), section);
        sections.findMiddleBase(section).ifPresent(base -> {
            sectionDao.save(base.calculateRemainSection(section));
            sectionDao.delete(base.getId());
        });
        return sectionDao.findById(sectionDao.save(section));
    }

    public Section findById(Long id) {
        checkService.checkSectionExist(id);
        return sectionDao.findById(id);
    }

    public List<Station> findStationsOfLine(Long lineId) {
        checkService.checkLineExist(lineId);
        return new Sections(sectionDao.findAllByLineId(lineId)).calculateStations();
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        checkService.checkLineExist(lineId);
        checkService.checkStationExist(stationId);
        Sections sections = Sections.forDelete(sectionDao.findAllByLineId(lineId));
        Station station = stationDao.findById(stationId);
        sections.findSide(station).ifPresentOrElse(section -> sectionDao.delete(section.getId()),
                () -> {
                    sectionDao.deleteAllBySections(sections.findLinks(station));
                    sectionDao.save(sections.calculateCombinedSection(station));
                });
    }

    @Transactional
    public void deleteStationById(Long stationId) {
        checkService.checkStationExist(stationId);
        validateStationNotLinked(stationId);
        stationDao.delete(stationId);
    }

    @Transactional
    public void deleteLineById(Long id) {
        checkService.checkLineExist(id);
        sectionDao.deleteAllByLineId(id);
        lineDao.delete(id);
    }

    private void validateStationNotLinked(Long stationId) {
        lineDao.findAll().stream()
                .map(this::getSections)
                .filter(sections -> !sections.findLinks(stationDao.findById(stationId)).isEmpty())
                .findAny()
                .ifPresent(section -> {
                    throw new IllegalArgumentException(ALREADY_IN_LINE_ERROR_MESSAGE);
                });
    }

    private Sections getSections(Line line) {
        return new Sections(sectionDao.findAllByLineId(line.getId()));
    }
}
