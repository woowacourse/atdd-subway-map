package wooteco.subway.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.FinalStations;
import wooteco.subway.line.LineDao;
import wooteco.subway.line.LineException;

@Service
@Transactional
public class SectionServiceTemp {
    private static final int LIMIT_NUMBER_OF_STATION_IN_LINE = 2;

    private final SectionDao sectionDao;
    private final LineDao lineDao;

    public SectionServiceTemp(final SectionDao sectionDao, final LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    public void addSection(long lineId, long front, long back, int distance) {
        addSection(new Section(lineId, front, back, distance));
    }

    public void addSection(final Section section) {
        if (isFinalSection(section.lineId(), section.front(), section.back())) {
            addFinalSection(section);
            return;
        }

        if (isMiddleSection(section.lineId(), section.front(), section.back())) {
            addMiddleSection(section);
            return;
        }

        throw new LineException("잘못된 구간 입력입니다.");
    }

    private boolean isFinalSection(final Long lineId, final Long front, final Long back) {
        final FinalStations finalStations = new FinalStations(lineDao.findUpStationId(lineId), lineDao.findDownStationId(lineId));
        return finalStations.isFinalSection(front, back);
    }

    private boolean isMiddleSection(final Long lineId, final Long front, final Long back) {
        return sectionDao.isExistingFrontStation(lineId, front) != sectionDao.isExistingBackStation(lineId, back);
    }

    private void addFinalSection(final Section section) {
        final Long lineId = section.lineId();

        final FinalStations before = new FinalStations(lineDao.findUpStationId(lineId), lineDao.findDownStationId(lineId));
        final FinalStations after = before.addStations(section.front(), section.back());

        lineDao.update(lineId, after.getUpStationId(), after.getDownStationId());
        sectionDao.save(lineId, section.front(), section.back(), section.distance());
    }

    private void addMiddleSection(final Section section) {
        final Long lineId = section.lineId();
        final Long front = section.front();
        final Long back = section.back();
        final int distance = section.distance();

        if (sectionDao.isExistingFrontStation(lineId, front)) {
            final Long beforeDownStation = sectionDao.backStationIdOf(lineId, front);
            final Distance beforeDistance = distance(lineId, front, beforeDownStation);
            sectionDao.updateBackStation(lineId, beforeDownStation, back, distance);
            sectionDao.save(lineId, back, beforeDownStation, beforeDistance.sub(distance).value());
            return;
        }

        final Long beforeUpStation = sectionDao.frontStationIdOf(lineId, back);
        final Distance beforeDistance = distance(lineId, beforeUpStation, back);
        sectionDao.updateFrontStation(lineId, beforeUpStation, front, distance);
        sectionDao.save(lineId, beforeUpStation, front, beforeDistance.sub(distance).value());
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        validateDeleteSection(lineId, stationId);

        if (isFinalSection(lineId, stationId, stationId)) {
            deleteFinalStation(lineId, stationId);
            return;
        }

        deleteMiddleSection(lineId, stationId);
    }

    private void deleteMiddleSection(Long lineId, Long stationId) {
        final Long front = sectionDao.frontStationIdOf(lineId, stationId);
        final Long back = sectionDao.backStationIdOf(lineId, stationId);

        final Distance distance = distance(lineId, front, stationId, back);
        sectionDao.updateBackStation(lineId, stationId, back, distance.value());
        sectionDao.deleteSection(lineId, stationId, back);
    }

    private void deleteFinalStation(Long lineId, Long stationId) {
        if (lineDao.isUpStation(lineId, stationId)) {
            final Long back = sectionDao.backStationIdOf(lineId, stationId);
            lineDao.updateUpStation(lineId, back);
            sectionDao.deleteSection(lineId, stationId, back);
        }

        if (lineDao.isDownStation(lineId, stationId)) {
            final Long front = sectionDao.frontStationIdOf(lineId, stationId);
            lineDao.updateDownStation(lineId, front);
            sectionDao.deleteSection(lineId, front, stationId);
        }
    }

    public Distance distance(final Long lineId, final Long front, final Long back) {
        return new Distance(sectionDao.findDistance(lineId, front, back));
    }

    public Distance distance(final Long lineId, final Long front, final Long middle, final Long back) {
        return distance(lineId, front, middle).add(distance(lineId, middle, back));
    }

    private void validateDeleteSection(final Long lineId, final Long stationId) {
        if (sectionDao.stationCountInLine(lineId) <= LIMIT_NUMBER_OF_STATION_IN_LINE) {
            throw new LineException("종점 뿐인 노선의 역을 삭제할 수 없습니다.");
        }

        if (!sectionDao.isExistingStation(lineId, stationId)) {
            throw new LineException("노선에 존재하지 않는 역을 삭제할 수 없습니다.");
        }
    }
}
