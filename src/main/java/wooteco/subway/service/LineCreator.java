package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.LineEntity;
import wooteco.subway.dto.SectionEntity;

@Service
public class LineCreator {
    private static final String ERROR_MESSAGE_NOT_EXISTS_ID = "존재하지 않는 지하철 노선 id입니다.";

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    LineCreator(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    Line createLine(Long lineId) {
        validateNotExists(lineId);
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

    private void validateNotExists(Long id) {
        if (!lineDao.existById(id)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NOT_EXISTS_ID);
        }
    }
}
