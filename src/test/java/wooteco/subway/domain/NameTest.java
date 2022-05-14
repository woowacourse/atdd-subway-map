package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.MaxNameLengthException;

class NameTest {
    
    @Test
    @DisplayName("255자를 초과한 이름을 생성하면 예외가 발생한다.")
    void createOverLengthName() {
        assertThatThrownBy(() -> new Name("asfljknasfebfgljkadsbngfjkl"
                + "dsbjkgvbnadsjkfgbnadsjbfnkadjsbfkjadsbfgkjbsadkjgabdskgbds"
                + "ajklgbnlasdknbglkadsnfglkadsnglkdsanbgvljkderswnbgjaewbrgf"
                + "kjabsdfgbasdgbsadlhnfgljkasdfgladsbhnfgjklaewrbhgkjabhgls"
                + "dahgqlwjkgbhdslgfbhasdfkljbghklajsdbgkjladsbngkjsadbgkasdbgkjsdbfg"
                + "kjasdbfgkjadsbfjkasdbfgkjasdbfgkjadswbgfkjdsabkjgbasdrkjgbasdfljkgbj"
                + "klasd"))
                .isInstanceOf(MaxNameLengthException.class);
    }
}