import {EVENT_TYPE} from "../../utils/constants.js";
import {colorSelectOptionTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminLine() {
    const $subwayLineList = document.querySelector("#subway-line-list");
    const $subwayLineNameInput = document.querySelector("#subway-line-name");
    const $subwayLineColorInput = document.querySelector("#subway-line-color");
    const $intervalTimeInput = document.querySelector("#interval-time");
    const $firstTimeInput = document.querySelector("#first-time");
    const $lastTimeInput = document.querySelector("#last-time");
    const $submitSubwayLineButton = document.querySelector(
        "#subway-line-create-form #submit-button"
    );
    const subwayLineModal = new Modal();

    let $updateLineItem = null;

    const onCreateSubwayLine = event => {
        event.preventDefault();
        const newSubwayLine = {
            name: $subwayLineNameInput.value,
            startTime: $firstTimeInput.value,
            endTime: $lastTimeInput.value,
            intervalTime: $intervalTimeInput.value,
            bgColor: $subwayLineColorInput.value
        };

        api.line.create(newSubwayLine)
            .then(line => {
                if (!line.name) {
                    alert("저장 실패!");
                    return;
                }

                $subwayLineList.insertAdjacentHTML(
                    "beforeend",
                    subwayLinesTemplate(line)
                );
            });
        subwayLineModal.toggle();
    };

    const onDeleteSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            $target.closest(".subway-line-item").remove();
        }
    };

    const onReadSubwayLineToUpdate = event => {
        const $target = event.target;
        const isUpdateButton = $target.classList.contains("mdi-pencil");
        if (!isUpdateButton) {
            return;
        }

        subwayLineModal.toggle();

        $submitSubwayLineButton.removeEventListener(
            EVENT_TYPE.CLICK,
            onCreateSubwayLine
        );
        $submitSubwayLineButton.addEventListener(
            EVENT_TYPE.CLICK,
            onUpdateSubwayLine
        );

        $updateLineItem = $target.closest(".subway-line-item");
        const targetId = $updateLineItem.dataset.lineId;

        api.line.getById(targetId)
            .then(line => {
                $subwayLineNameInput.value = line.name;
                $firstTimeInput.value = line.startTime;
                $lastTimeInput.value = line.endTime;
                $intervalTimeInput.value = line.intervalTime;
                $subwayLineColorInput.value = line.bgColor;
            });
    };

    const onUpdateSubwayLine = event => {
        event.preventDefault();
        const updatedSubwayLine = {
            name: $subwayLineNameInput.value,
            startTime: $firstTimeInput.value,
            endTime: $lastTimeInput.value,
            intervalTime: $intervalTimeInput.value,
            bgColor: $subwayLineColorInput.value
        };

        api.line
            .update(updatedSubwayLine, $updateLineItem.dataset.lineId)
            .then(line => {
                //TODO : 업데이트 결과 반영.
                $updateLineItem.insertAdjacentHTML(
                    "afterend",
                    subwayLinesTemplate(line)
                );
                $updateLineItem.remove();
            });
        subwayLineModal.toggle();


        $submitSubwayLineButton.removeEventListener(
            EVENT_TYPE.CLICK,
            onUpdateSubwayLine
        );
        $submitSubwayLineButton.addEventListener(
            EVENT_TYPE.CLICK,
            onCreateSubwayLine
        );
    };

    const initDefaultSubwayLines = () => {
        api.line.get()
            .then(lines =>
                lines.map(line => {
                    $subwayLineList.insertAdjacentHTML(
                        "beforeend",
                        subwayLinesTemplate(line)
                    );
                }));
    };

    const initEventListeners = () => {
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onReadSubwayLineToUpdate);
        $submitSubwayLineButton.addEventListener(
            EVENT_TYPE.CLICK,
            onCreateSubwayLine
        );
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
        initEventListeners();
        initCreateSubwayLineForm();
    };
}

const adminLine = new AdminLine();
adminLine.init();
