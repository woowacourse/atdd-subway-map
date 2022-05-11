package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineEntity;
import wooteco.subway.dto.SectionEntity;
import wooteco.subway.dto.info.RequestCreateSectionInfo;
import wooteco.subway.dto.info.RequestDeleteSectionInfo;

@Service
public class SectionService {
    private static final String ERROR_MESSAGE_NOT_EXISTS_ID = "존재하지 않는 지하철 노선 id입니다.";

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public void save(RequestCreateSectionInfo requestCreateSectionInfo) {
        Long lineId = requestCreateSectionInfo.getLineId();
        Long upStationId = requestCreateSectionInfo.getUpStationId();
        Long downStationId = requestCreateSectionInfo.getDownStationId();
        Integer distance = requestCreateSectionInfo.getDistance();

        validateNotExistsLine(lineId);
        validateNotExistStation(upStationId);
        validateNotExistStation(downStationId);

        Section section = new Section(stationDao.getStation(upStationId), stationDao.getStation(downStationId),
            distance);
        LineEntity lineEntity = lineDao.find(lineId);
        Line line = new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(),
            new Sections(findSections(lineEntity.getId())));
        line.checkCanAddAndUpdate(section);

        Sections sections = line.getSections();
        sectionDao.save(line.getId(), section);
        sections.forEach(section1 -> sectionDao.update(line.getId(), section1));
    }

    @Transactional
    public void delete(RequestDeleteSectionInfo requestDeleteSectionInfo) {
        Long lineId = requestDeleteSectionInfo.getLineId();
        Long stationId = requestDeleteSectionInfo.getStationId();

        validateNotExistsLine(lineId);
        validateNotExistStation(stationId);

        Line line = createLine(lineId);
        Station station = stationDao.getStation(stationId);
        Section deletedSection = line.delete(station);

        sectionDao.delete(lineId, deletedSection);
        line.getSections().forEach(section -> sectionDao.update(lineId, section));
    }

    private void validateNotExistsLine(Long id) {
        if (!lineDao.existById(id)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NOT_EXISTS_ID);
        }
    }

    private void validateNotExistStation(Long id) {
        if (!stationDao.existById(id)) {
            throw new IllegalArgumentException("존재하지 않는 역을 지나는 구간은 만들 수 없습니다.");
        }
    }

    private Line createLine(Long lineId) {
        LineEntity lineEntity = lineDao.find(lineId);
        return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(),
            new Sections(findSections(lineEntity.getId())));
    }

    private List<Section> findSections(Long lineId) {
        List<SectionEntity> sectionEntities = sectionDao.findByLine(lineId);
        return sectionEntities.stream()
            .map(this::findSection)
            .collect(Collectors.toList());
    }

    private Section findSection(SectionEntity sectionEntity) {
        return new Section(sectionEntity.getId(), stationDao.getStation(sectionEntity.getUpStationId())
            , stationDao.getStation(sectionEntity.getDownStationId()), sectionEntity.getDistance());
    }
}
