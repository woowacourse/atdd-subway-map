package wooteco.subway.util;

import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CollectorsUtils {

    public static <T> Collector<T, ?, Optional<T>> findOneCertainly() {
        return Collectors.collectingAndThen(
            Collectors.toList(),
            list -> {
                if (list.size() == 1) {
                    return Optional.of(list.get(0));
                }
                return Optional.empty();
            }
        );
    }
}
