package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionValidator;
import wooteco.subway.exception.EntityNotFoundException;

import java.util.Optional;

@Transactional
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

        final Line line = findLineByLineId(section.getLineId());
        if (isSectionTop(line, section)) {
            return saveSectionAtTop(section);
        }

        if (isSectionBottom(line, section)) {
            return saveSectionAtBottom(section);
        }

        return saveSectionAtMiddle(section);
    }

    @Transactional(readOnly = true)
    void checkStationIsExist(final Section section) {
        final boolean existsUpStation = stationDao.findById(section.getUpStationId()).isPresent();
        final boolean existsDownStation = stationDao.findById(section.getDownStationId()).isPresent();
        if (!(existsUpStation && existsDownStation)) {
            throw new EntityNotFoundException("해당 ID와 일치하는 역이 존재하지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public Line findLineByLineId(final Long lineId) {
        return lineDao.findById(lineId).orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 노선이 존재하지 않습니다."));
    }

    private boolean isSectionTop(final Line line, final Section section) {
        return isStationTop(line, section.getDownStationId());
    }

    private boolean isStationTop(final Line line, final Long stationId) {
        return line.getTopStationId().equals(stationId);
    }

    Section saveSectionAtTop(final Section section) {
        lineDao.updateTopStationId(section);
        return sectionDao.save(section);
    }

    private boolean isSectionBottom(final Line line, final Section section) {
        return isStationBottom(line, section.getUpStationId());
    }

    boolean isStationBottom(final Line line, final Long stationId) {
        return line.getBottomStationId().equals(stationId);
    }

    Section saveSectionAtBottom(final Section section) {
        lineDao.updateBottomStationId(section);
        return sectionDao.save(section);
    }

    Section saveSectionAtMiddle(final Section section) {
        final Long lineId = section.getLineId();
        new SectionValidator(sectionDao.findAllByLineId(lineId), section).validate();

        final Optional<Section> sectionByUpStation = sectionDao.findByLineIdAndUpStationId(section);
        if (sectionByUpStation.isPresent()) {
            final Section updateSection = section.subtractDistance(sectionByUpStation.get());
            sectionDao.updateUpStationAndDistance(updateSection);
            return sectionDao.save(section);
        }

        final Section sectionByDownStation = sectionDao.findByLineIdAndDownStationId(section)
                                                       .orElseThrow(() -> new IllegalStateException("하행역과 상행역이 모두 존재하지 않습니다."));

        final Section updateSection = section.subtractDistance(sectionByDownStation);
        sectionDao.updateDownStationAndDistance(updateSection);
        return sectionDao.save(section);
    }

    @Transactional(readOnly = true)
    public Section findByLineIdAndId(final Long lineId, final Long sectionId) {
        return sectionDao.findByLineIdAndId(lineId, sectionId)
                         .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 구간이 존재하지 않습니다."));
    }

    public void deleteSection(final Section section) {
        final Long lineId = section.getLineId();
        final Line line = findLineByLineId(lineId);
        if (isStationTop(line, section.getUpStationId())) {
            deleteTopStation(section);
            return;
        }

        if (isStationBottom(line, section.getUpStationId())) {
            deleteBottomStation(section);
            return;
        }

        deleteMiddleStation(section);
    }

    void deleteTopStation(final Section section) {
        final Long downStationId = sectionDao.findByLineIdAndUpStationId(section)
                                             .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 구간이 존재하지 않습니다."))
                                             .getDownStationId();

        lineDao.updateTopStationId(section.updateUpStationId(downStationId));
        sectionDao.deleteByLineIdAndUpStationId(section);
    }

    void deleteBottomStation(final Section section) {
        final Long upStationId = sectionDao.findByLineIdAndDownStationId(section)
                                           .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 구간이 존재하지 않습니다."))
                                           .getUpStationId();

        lineDao.updateBottomStationId(section.updateDownStationId(upStationId));
        sectionDao.deleteByLineIdAndDownStationId(section);
    }

    void deleteMiddleStation(final Section section) {
        final Section upSection = sectionDao.findByLineIdAndDownStationId(section)
                                            .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 구간이 존재하지 않습니다."));

        final Section downSection = sectionDao.findByLineIdAndUpStationId(section)
                                              .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 구간이 존재하지 않습니다."));

        final int distance = upSection.getDistance() + downSection.getDistance();
        final Section updateSection = section.updateUpStationId(upSection.getUpStationId())
                                             .updateDownStationId(downSection.getDownStationId())
                                             .updateDistance(distance);

        sectionDao.deleteByLineIdAndUpStationId(updateSection);
        sectionDao.updateByLineIdAndDownStationId(updateSection);
    }
}
