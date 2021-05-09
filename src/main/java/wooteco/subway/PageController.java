package wooteco.subway;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 이 클래스는 프론트엔드 코드에서 활용하는 코드 입니다. 수정하지 마세요.
 */
@Controller
public class PageController {
    @GetMapping(value = {
            "/",
            "/stations",
            "/lines",
            "/sections",
            "/path",
            "/login",
            "/join",
            "/mypage",
            "/mypage/edit",
            "/favorites"}, produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return "index";
    }
}
