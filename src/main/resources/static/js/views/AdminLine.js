import {EVENT_TYPE} from "../../utils/constants.js";
import {colorSelectOptionTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminLine() {
    const $subwayLineList = document.querySelector("#subway-line-list");
    const $subwayLineNameInput = document.querySelector("#subway-line-title");
    const $subwayLineFirstTimeInput = document.querySelector("#first-time");
    const $subwayLineLastTimeInput = document.querySelector("#last-time");
    const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");
    const $subwayLineColorInput = document.querySelector("#subway-line-color");

    const $createSubwayLineButton = document.querySelector(
        "#subway-line-create-form #submit-button"
    );

    const subwayLineModal = new Modal();

    // todo: 노선 이름을 클릭했을 때 나오도록 수정!
    const onViewSubwayInfo = event => {
        const $target = event.target;
        const isSubwayTitle = $target.classList.contains("subway-line-title");
        if (isSubwayTitle) {
            const $id = $target.id;

            api.line.getLineById($id).then(data => {
                    const $subwayLineFirstTime = document.querySelector("#start-subway-time");
                    $subwayLineFirstTime.innerText = data.startTime;
                    const $subwayLineLastTime = document.querySelector("#last-subway-time");
                    $subwayLineLastTime.innerText = data.endTime;
                    const $subwayLineIntervalTime = document.querySelector("#subway-interval-time");
                    $subwayLineIntervalTime.innerText = data.intervalTime + "분";
                }
            );
        }
    };

    const onCreateSubwayLine = event => {
        event.preventDefault();

        const data = {
            name: $subwayLineNameInput.value,
            startTime: $subwayLineFirstTimeInput.value,
            endTime: $subwayLineLastTimeInput.value,
            intervalTime: $subwayLineIntervalTimeInput.value,
            bgColor: $subwayLineColorInput.value
        };

        // todo: get을 해오면, 기존에 보여지는 라인에 붙어서 다시 라인이 나옴. 만든 한 줄만 가져왔으!!
        api.line.create(data)
            .then(subwayLine => {
                $subwayLineList.insertAdjacentHTML(
                    "beforeend",
                    subwayLinesTemplate(subwayLine)
                );
                subwayLineModal.toggle();
            })
            // todo: 중복 발생 시에도 경고?
            .catch(error => {
                alert("에러가 발생하였습니다!");
            })

        // todo: 입력창 비우기 코드 추가!
        subwayLineModal.toggle();
        $subwayLineNameInput.value = "";
        $subwayLineColorInput.value = "";
        $subwayLineFirstTimeInput.value = "";
        $subwayLineLastTimeInput.value = "";
        $subwayLineIntervalTimeInput.value = "";
    };

    // todo: delete 기능 구현!!
    const onDeleteSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            const $id = $target.id;

            api.line.delete($id).then();
            $target.closest(".subway-line-item").remove();
        }
    };

    const onUpdateSubwayLine = event => {
        const $target = event.target;
        const isUpdateButton = $target.classList.contains("mdi-pencil");
        if (isUpdateButton) {
            subwayLineModal.toggle();

            // todo: getLineById로 갖고 온 노선 1개의 데이터를 수정 창에 입력시킴!!
            // todo: 그런데 시간이 안 찍힘!!!
            const $id = $target.id;

            api.line.getLineById($id).then(data => {
                const $subwayLineTitle = document.getElementById("subway-line-title");
                $subwayLineTitle.value = data.title;
                const $subwayLineFirstTime = document.getElementById("first-time");
                $subwayLineFirstTime.innerText = data.startTime;
                const $subwayLineLastTime = document.getElementById("last-time");
                $subwayLineLastTime.innerText = data.endTime;
                const $subwayLineIntervalTime = document.getElementById("interval-time");
                $subwayLineIntervalTime.innerText = data.intervalTime + "분";
                const $subwayLineColor = document.getElementById("subway-line-color");
                $subwayLineColor.value = data.bgColor;
            });

            // todo: 확인 버튼에 이벤트 걸기. 함수 인자로 id와 수정할 데이터 전달 필요!
            // todo: 확인 버튼이 생성 버튼과 중복되므로 post와 put의 분기를 추가해야 한다.
            // todo: 기존에 버튼이 갖고 있던 이벤트를 remove 메서드로 제거하고 사용?
            const $confirmButton = document.getElementById("submit-button");
            const data = {
                name: $subwayLineNameInput.value,
                startTime: $subwayLineFirstTimeInput.value,
                endTime: $subwayLineLastTimeInput.value,
                intervalTime: $subwayLineIntervalTimeInput.value,
                bgColor: $subwayLineColorInput.value
            };
            $confirmButton.addEventListener(EVENT_TYPE.CLICK, function() {
                onEditSubwayLine($id, data);
            });
        }
    };

    function onEditSubwayLine(id, data) {
        api.line.update(id, data).then();
    };

    const initDefaultSubwayLines = () => {
        api.line.get().then(subwayLines => subwayLines.forEach(
            subwayLine => {
                $subwayLineList.insertAdjacentHTML(
                    "beforeend",
                    subwayLinesTemplate(subwayLine)
                );
            }
        ));
    };

    const initEventListeners = () => {
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLine);
        $createSubwayLineButton.addEventListener(
            EVENT_TYPE.CLICK,
            onCreateSubwayLine
        );
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onViewSubwayInfo);
    };

    const onSelectColorHandler = event => {
        event.preventDefault();
        const $target = event.target;
        if ($target.classList.contains("color-select-option")) {
            document.querySelector("#subway-line-color").value =
                $target.dataset.color;
        }
    };

    const initCreateSubwayLineForm = () => {
        const $colorSelectContainer = document.querySelector(
            "#subway-line-color-select-container"
        );
        const colorSelectTemplate = subwayLineColorOptions
            .map((option, index) => colorSelectOptionTemplate(option, index))
            .join("");
        $colorSelectContainer.insertAdjacentHTML("beforeend", colorSelectTemplate);
        $colorSelectContainer.addEventListener(
            EVENT_TYPE.CLICK,
            onSelectColorHandler
        );
    };

    this.init = () => {
        initDefaultSubwayLines();
        initCreateSubwayLineForm();
        initEventListeners();
    };
}

const adminLine = new AdminLine();
adminLine.init();
