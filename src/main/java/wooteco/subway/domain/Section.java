package wooteco.subway.domain;

import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.ClientException;

import java.util.Objects;

public class Section {

    private final static int BLANK = 0;
    private final static Long EMPTY = null;
    private static final long BASIC_ID = 0L;

    private final Long id;
    private final Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validateNull(lineId, upStationId, downStationId, distance);
        validateMinimumRange(lineId, upStationId, downStationId, distance);
        validateUpAndDownSameStation(upStationId, downStationId);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(BASIC_ID, lineId, upStationId, downStationId, distance);
    }

    private void validateMinimumRange(Long lineId, Long upStationId, Long downStationId, int distance) {
        if (lineId == BLANK || upStationId == BLANK || downStationId == BLANK || distance == BLANK) {
            throw new ClientException("지하철 노선 Id와 상행, 하행 역, 거리는 0 이상의 값이어야 합니다.");
        }
    }

    private void validateNull(Long lineId, Long upStationId, Long downStationId, int distance) {
        if (lineId == EMPTY || upStationId == EMPTY || downStationId == EMPTY || distance == BLANK) {
            throw new ClientException("지하철 노선 Id와 상행, 하행 역을 입력해주세요.");
        }
    }

    private void validateUpAndDownSameStation(Long upStationId, Long downStationId) {
        if (Objects.equals(upStationId, downStationId)) {
            throw new ClientException("상행역과 하행역이 같을 수 없습니다.");
        }
    }
    
    public boolean isSameUpStationId(SectionRequest request) {
        return upStationId.equals(request.getUpStationId());
    }

    public boolean isSameDownStationId(SectionRequest request) {
        return downStationId.equals(request.getDownStationId());
    }

    public boolean hasSameStationId(SectionRequest request) {
        return isSameUpStationId(request) || isSameDownStationId(request);
    }

    public boolean isPossibleDistanceCondition(SectionRequest sectionRequest) {
        return distance > sectionRequest.getDistance();
    }

    public Section createBySameStationId(Long id, SectionRequest request) {
        if (isSameUpStationId(request)) {
            return new Section(id, request.getDownStationId(), downStationId, distance - request.getDistance());
        }
        return new Section(id, upStationId, request.getUpStationId(), distance - request.getDistance());
    }

    public void updateSameStationId(SectionRequest request) {
        if (isSameUpStationId(request)) {
            this.downStationId = request.getDownStationId();
            this.distance = request.getDistance();
        }
        if (isSameDownStationId(request)) {
            this.upStationId = request.getUpStationId();
            this.distance = request.getDistance();
        }
    }

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
