package wooteco.subway.domain;

import java.util.List;
import wooteco.subway.exception.SubwayUnknownException;
import wooteco.subway.exception.validation.SectionDistanceExceedException;

final class SectionJoinUtil {

    private static final List<SectionResult> ONE_OF_END_EXTENDED = List.of(SectionResult.UP_EXTENDED,
            SectionResult.DOWN_EXTENDED);

    private SectionJoinUtil() {
    }

    public static SectionResult join(Section joinPoint, Section input) {
        SectionResult joinTarget = checkIfEndExtension(joinPoint, input);
        if (ONE_OF_END_EXTENDED.contains(joinTarget)) {
            return joinTarget;
        }

        validateDistance(joinPoint, input);

        // 상행역 기준으로 가운데 역 추가
        if (joinPoint.hasSameUpStation(input)) {
            joinPoint.shortenUpStation(input);
            return SectionResult.MIDDLE_ADDED;
        }

        // 하행역 기준 가운데 역 추가
        if (joinPoint.hasSameDownStation(input)) {
            joinPoint.shortenDownStation(input);
            return SectionResult.MIDDLE_ADDED;
        }

        throw new SubwayUnknownException("구간 확장 처리 중 예외가 발생했습니다");
    }

    private static SectionResult checkIfEndExtension(Section joinPoint, Section input) {
        if (joinPoint.upStationIsSameToDownStation(input)) {
            return SectionResult.UP_EXTENDED;
        }

        if (joinPoint.downStationIsSameToUpStation(input)) {
            return SectionResult.DOWN_EXTENDED;
        }

        return SectionResult.NOT_BOTH_END_EXTENSION;
    }

    private static void validateDistance(Section joinPoint, Section input) {
        if (!joinPoint.isWider(input)) {
            throw new SectionDistanceExceedException(input.getDistance());
        }
    }
}
