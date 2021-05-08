package wooteco.subway.line.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.domain.*;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.SectionAddRequest;
import wooteco.subway.line.entity.SectionEntity;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationDao;
import wooteco.subway.station.dto.StationResponse;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineResponse save(final LineRequest lineRequest) {
        LineEntity savedLineEntity = lineDao.save(new LineEntity(lineRequest.getName(), lineRequest.getColor()));
        Station upStation = findStationById(lineRequest.getUpStationId());
        Station downStation = findStationById(lineRequest.getDownStationId());

        Line line = new Line(savedLineEntity.id(), savedLineEntity.name(), savedLineEntity.color());

        SectionEntity sectionEntity = sectionDao.save(new SectionEntity(line.getId(), upStation.getId(), downStation.getId(), lineRequest.getDistance()));

        Section section = new Section(sectionEntity.getId(), line, upStation, downStation, sectionEntity.getDistance());

        return new LineResponse(line.getId(), line.nameAsString(), line.getColor(), toStationsResponses(section.line()));
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(final Long id) {
        LineEntity findLineEntity = findLineEntityById(id);

        Line line = new Line(findLineEntity.id(), findLineEntity.name(), findLineEntity.color());

        List<SectionEntity> sectionEntities = sectionDao.findByLineId(id);
        sectionEntities.forEach(sectionEntity -> new Section(sectionEntity.getId(), line, findStationById(sectionEntity.getUpStationId()), findStationById(sectionEntity.getDownStationId()), sectionEntity.getDistance()));

        return new LineResponse(line.getId(), line.nameAsString(), line.getColor(), toStationsResponses(line));
    }

    private List<StationResponse> toStationsResponses(final Line line) {
        return line.getSections()
                .stream()
                .map(singleSection -> Arrays.asList(singleSection.upStation(), singleSection.downStation()))
                .flatMap(Collection::stream)
                .distinct()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    private LineEntity findLineEntityById(Long id) {
        return lineDao.findById(id).orElseThrow(() -> new IllegalArgumentException("없는 노선임!"));
    }

    private Station findStationById(Long stationId) {
        return stationDao.findById(stationId).orElseThrow(() -> new IllegalStateException("없는 역임!"));
    }

    @Transactional
    public void addSection(final Long lineId, final SectionAddRequest sectionAddRequest) {
        // TODO : 예외
        //  lineId가 존재하는지
        //  line의 section에 upstationId와 downStationId 둘다 존재하는지 - 노선의 구간에 이미 등록되어있음
        //  upstationId 또는 downStationId로 section을 찾는데, 찾은 section의 distance가 sectionAddRequest의 distance보다 작거나 같은 경우


        // TODO : line의 section에 sectionAddRequest의 upstationId가 존재하는지
        //  존재하면 sectionAddRequest의 upstationId로 section을 찾고
        //  찾은 section의 upstationId를 sectionAddRequest의 downStationId로 수정한다.
        //  찾은 section의 distance를 sectionAddRequest의 distance를 뺀 값으로 수정한다.
        //

        // TODO : line의 section에 sectionAddRequest의 downStationId가 존재하는지
        //  존재하면 sectionAddRequest의 downStationId로 section을 찾고
        //  찾은 section의 downStationId를 sectionAddRequest의 upStationId로 수정한다.
        //  찾은 section의 distance를 sectionAddRequest의 distance를 뺀 값으로 수정한다.

        // TODO : section save

        sectionDao.save(sectionAddRequest.toEntity(lineId));
    }
}
