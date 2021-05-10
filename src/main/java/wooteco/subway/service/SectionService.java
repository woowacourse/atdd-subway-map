package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionValidator;
import wooteco.subway.exception.EntityNotFoundException;

import java.util.Optional;

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
        return isStationTop(line, section.getDownStationId());
    }

    private boolean isStationTop(final Line line, final Long stationId) {
        return line.getTopStationId().equals(stationId);
    }

    private Section saveSectionAtTop(final Section section) {
        lineDao.updateTopStationId(section.getLineId(), section.getUpStationId());
        return sectionDao.save(section);
    }

    private boolean isSectionBottom(final Line line, final Section section) {
        return isStationBottom(line, section.getDownStationId());
    }

    private boolean isStationBottom(final Line line, final Long stationId) {
        return line.getBottomStationId().equals(stationId);
    }

    private Section saveSectionAtBottom(final Section section) {
        lineDao.updateBottomStationId(section.getLineId(), section.getDownStationId());
        return sectionDao.save(section);
    }

    private Section saveSectionAtMiddle(final Section section) {
        final Long lineId = section.getLineId();
        new SectionValidator(sectionDao.findAllByLineId(lineId), section).validate();

        final Optional<Section> sectionByUpStation = sectionDao.findByLineIdAndUpStationId(lineId,
                section.getUpStationId());
        if (sectionByUpStation.isPresent()) {
            final Section updateSection = section.subtractDistance(sectionByUpStation.get());
            sectionDao.updateUpStationAndDistance(updateSection);
            return sectionDao.save(section);
        }

        final Section sectionByDownStation = sectionDao.findByLineIdAndDownStationId(lineId, section.getDownStationId())
                                                       .orElseThrow(() -> new IllegalStateException("하행역과 상행역이 모두 " + "존재하지 않습니다."));

        final Section updateSection = section.subtractDistance(sectionByDownStation);
        sectionDao.updateDownStationAndDistance(updateSection);
        return sectionDao.save(section);
    }

    public Section findByLineIdAndId(final Long lineId, final Long sectionId) {
        return sectionDao.findByLineIdAndId(lineId, sectionId)
                         .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 구간이 존재하지 않습니다."));
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        final Line line = findLineByLineIdId(lineId);
        if (isStationTop(line, stationId)) {
            deleteTopStation(lineId, stationId);
        }

        if (isStationBottom(line, stationId)) {
            deleteBottomStation(lineId, stationId);
        }
    }

    private void deleteTopStation(final Long lineId, final Long stationId) {
        final Section section = sectionDao.findByLineIdAndUpStationId(lineId, stationId)
                                          .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 구간이 존재하지 " +
                                                  "않습니다."));

        lineDao.updateTopStationId(lineId, section.getDownStationId());
        sectionDao.deleteByLineIdAndUpStationId(lineId, stationId);
    }

    private void deleteBottomStation(final Long lineId, final Long stationId) {
        final Section section = sectionDao.findByLineIdAndDownStationId(lineId, stationId)
                                          .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 구간이 존재하지 " +
                                                  "않습니다."));

        lineDao.updateBottomStationId(lineId, section.getUpStationId());
        sectionDao.deleteByLineIdAndDownStationId(lineId, stationId);
    }
}
