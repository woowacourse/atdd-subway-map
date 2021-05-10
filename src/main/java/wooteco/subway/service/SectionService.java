package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionValidator;
import wooteco.subway.exception.EntityNotFoundException;

@Service
public class SectionService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(final LineDao lineDao, final SectionDao sectionDao, final StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Section save(final Section section) {
        checkStationIsExist(section);

        final Line line = findLineByLineIdId(section.getLineId());
        if (isSectionTop(line, section)) {
            return saveSectionAtTop(section);
        }

        if (isSectionBottom(line, section)) {
            return saveSectionAtBottom(section);
        }

        return saveSectionAtMiddle(section);
    }

    private void checkStationIsExist(final Section section) {
        final boolean existsUpStation = stationDao.findById(section.getUpStationId()).isPresent();
        final boolean existsDownStation = stationDao.findById(section.getDownStationId()).isPresent();
        if (!(existsUpStation && existsDownStation)) {
            throw new EntityNotFoundException("해당 ID와 일치하는 역이 존재하지 않습니다.");
        }
    }

    private Line findLineByLineIdId(final Long lineId) {
        return lineDao.findById(lineId).orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 노선이 존재하지 않습니다."));
    }

    private boolean isSectionTop(final Line line, final Section section) {
        return line.getTopStationId().equals(section.getDownStationId());
    }

    private Section saveSectionAtTop(final Section section) {
        lineDao.updateTopStationId(section.getLineId(), section.getUpStationId());
        return sectionDao.save(section);
    }

    private boolean isSectionBottom(final Line line, final Section section) {
        return line.getBottomStationId().equals(section.getUpStationId());
    }

    private Section saveSectionAtBottom(final Section section) {
        lineDao.updateBottomStationId(section.getLineId(), section.getDownStationId());
        return sectionDao.save(section);
    }

    private Section saveSectionAtMiddle(final Section section) {
        new SectionValidator(sectionDao.findAllByLineId(section.getLineId()), section).validate();
        final boolean canSaveSectionAtUpStationOnLine = sectionDao.findSectionByUpStation(section)
                                                                  .isPresent();

        if (canSaveSectionAtUpStationOnLine) {
            sectionDao.updateUpStationToDownStation(section.getUpStationId(), section.getDownStationId());
            return sectionDao.save(section);
        }

        sectionDao.updateDownStationToUpStation(section.getDownStationId(), section.getUpStationId());
        return sectionDao.save(section);
    }
}
