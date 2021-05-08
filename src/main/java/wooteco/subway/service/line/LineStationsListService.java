package wooteco.subway.service.line;


import java.util.ArrayList;
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
        List<Section> sectionsOfLine = sectionDao.findAllByLineId(lineId);
        List<Long> stationIdsInOrder = new LinkedList<>();
        Section firstSection = sectionsOfLine.get(0);
        stationIdsInOrder.add(firstSection.getUpStationId());
        stationIdsInOrder.add(firstSection.getDownStationId());

        Long upStationId = firstSection.getUpStationId();
        Long downStationId = firstSection.getDownStationId();
        while (stationIdsInOrder.size() < sectionsOfLine.size() + 1) {
            for (Section section : sectionsOfLine) {
                if (section.getDownStationId().equals(upStationId)) {
                    stationIdsInOrder.add(0, section.getUpStationId());
                    upStationId = section.getUpStationId();
                }
                if (section.getUpStationId().equals(downStationId)) {
                    stationIdsInOrder.add(section.getDownStationId());
                    downStationId = section.getDownStationId();
                }
            }
        }

        List<Station> stationsInOrder = new ArrayList<>();
        for (Long stationId : stationIdsInOrder) {
            Station station = stationDao.findById(stationId)
                .orElseThrow(() -> new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "노선 역 목록 조회 에러"));
            stationsInOrder.add(station);
        }

        List<StationResponseDto> stationResponseDtosInOrder = stationsInOrder.stream()
            .map(StationResponseDto::new)
            .collect(Collectors.toList());

        Line line = lineDao.findById(lineId)
            .orElseThrow(() -> new HttpException(HttpStatus.BAD_REQUEST, "존재하지 않는 노선 id 입니다."));
        return new LineStationsListResponseDto(line.getId(), line.getName(), line.getColor(), stationResponseDtosInOrder);
    }
}
