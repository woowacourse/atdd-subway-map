package wooteco.subway.domain.section;

import java.util.List;
import java.util.Optional;

public interface DeletionStrategy {
    void delete(List<Section> sections, Long lineId, Long stationId);

    Optional<Section> fixDisconnectedSection(List<Section> sections, Long lineId, Long stationId);
}
