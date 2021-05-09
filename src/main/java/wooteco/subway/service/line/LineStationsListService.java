package wooteco.subway.service.line;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.controller.dto.response.line.LineStationsListResponseDto;
import wooteco.subway.controller.dto.response.station.StationResponseDto;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.HttpException;

@Transactional
@Service
public class LineStationsListService {
    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final LineDao lineDao;

    public LineStationsListService(StationDao stationDao, SectionDao sectionDao, LineDao lineDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    public LineStationsListResponseDto getAllStationsInOrderListByLineId(Long lineId) {
        Line line = getLine(lineId);
        List<Section> sectionsOfLine = sectionDao.findAllByLineId(lineId);
        Section firstSection = sectionsOfLine.get(0);
        List<Long> stationIdsInOrder = new LinkedList<>(Arrays.asList(firstSection.getUpStationId(), firstSection.getDownStationId()));
        setStationIdsInOrder(sectionsOfLine, stationIdsInOrder);
        List<Station> stationsInOrder = getStationsInOrder(stationIdsInOrder);
        List<StationResponseDto> stationResponseDtosInOrder = getStationResponseDtosFromStationsInOrder(stationsInOrder);
        return new LineStationsListResponseDto(line.getId(), line.getName(), line.getColor(), stationResponseDtosInOrder);
    }

    private Line getLine(Long lineId) {
        return lineDao.findById(lineId)
            .orElseThrow(() -> new HttpException(HttpStatus.BAD_REQUEST, "존재하지 않는 노선 id 입니다."));
    }

    private void setStationIdsInOrder(List<Section> sectionsOfLine, List<Long> stationIdsInOrder) {
        int numberOfStationsInLine = sectionsOfLine.size() + 1;
        while (stationIdsInOrder.size() < numberOfStationsInLine) {
            checkStationsOfLineAndSetIdsInOrder(sectionsOfLine, stationIdsInOrder);
        }
    }

    private void checkStationsOfLineAndSetIdsInOrder(List<Section> sectionsOfLine, List<Long> stationIdsInOrder) {
        Long firstId = stationIdsInOrder.get(0);
        Long lastId = stationIdsInOrder.get(stationIdsInOrder.size() - 1);
        for (Section section : sectionsOfLine) {
            firstId = addStationIdToFirst(stationIdsInOrder, firstId, section);
            lastId = addStationIdToLast(stationIdsInOrder, lastId, section);
        }
    }

    private Long addStationIdToFirst(List<Long> stationIdsInOrder, Long firstId, Section section) {
        if (section.getDownStationId().equals(firstId)) {
            stationIdsInOrder.add(0, section.getUpStationId());
            firstId = section.getUpStationId();
        }
        return firstId;
    }

    private Long addStationIdToLast(List<Long> stationIdsInOrder, Long lastId, Section section) {
        if (section.getUpStationId().equals(lastId)) {
            stationIdsInOrder.add(section.getDownStationId());
            lastId = section.getDownStationId();
        }
        return lastId;
    }

    private List<Station> getStationsInOrder(List<Long> stationIdsInOrder) {
        List<Station> stationsInOrder = new ArrayList<>();
        for (Long stationId : stationIdsInOrder) {
            Station station = stationDao.findById(stationId)
                .orElseThrow(() -> new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "노선 역 목록 조회 에러"));
            stationsInOrder.add(station);
        }
        return stationsInOrder;
    }

    private List<StationResponseDto> getStationResponseDtosFromStationsInOrder(List<Station> stationsInOrder) {
        return stationsInOrder.stream()
            .map(StationResponseDto::new)
            .collect(Collectors.toList());
    }
}
