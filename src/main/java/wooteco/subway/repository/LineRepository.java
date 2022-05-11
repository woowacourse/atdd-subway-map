package wooteco.subway.repository;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.service.dto.LineDto;
import wooteco.subway.service.dto.SectionDto;

@Repository
public class LineRepository {

    private static final String LINE_DUPLICATED = "이미 존재하는 노선입니다. ";

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineRepository(final LineDao lineDao, final StationDao stationDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public Line save(final LineDto lineDto) {
        validateLineData(lineDto);
        final Long upStationId = lineDto.getUpStationId();
        final Long downStationId = lineDto.getDownStationId();
        final int distance = lineDto.getDistance();
        try {
            final Line line =  lineDao.save(lineDto);
            sectionDao.save(line.getId(), new SectionDto(upStationId, downStationId, distance));
            final Section newSection = new Section(stationDao.findById(upStationId),
                    stationDao.findById(downStationId), distance);
            line.addSection(newSection);
            return line;
        } catch (DuplicateKeyException e) {
            throw new IllegalStateException(LINE_DUPLICATED + lineDto);
        }
    }

    private void validateLineData(final LineDto lineDto) {
        checkExistedStation(lineDto.getUpStationId());
        checkExistedStation(lineDto.getDownStationId());
    }

    private void checkExistedStation(final Long stationId) {
        try {
            stationDao.findById(stationId);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalStateException("잘못된 역 아이디입니다. id=" + stationId);
        }
    }
}
