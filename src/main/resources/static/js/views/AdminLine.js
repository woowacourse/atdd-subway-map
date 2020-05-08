import {EVENT_TYPE} from "../../utils/constants.js";
import {colorSelectOptionTemplate, subwayLineInfoTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";
import {replaceEventListener} from "../../utils/util";

function AdminLine() {
    const $subwayLineInfo = document.querySelector(".lines-info");
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

    const renderSubwayLineInfo = line => {
        $subwayLineInfo.innerHTML = "";
        $subwayLineInfo.insertAdjacentHTML(
            "beforeend",
            subwayLineInfoTemplate(line)
        );
    }

    const onShowSubwayLine = event => {
        const $target = event.target.closest(".subway-line-item");
        event.preventDefault();

        api.line.getById($target.dataset.lineId)
            .then(data => data.json())
            .then(line => renderSubwayLineInfo(line))
            .catch(error => console.log(error));
    }

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
            .then(data => data.json())
            .then(line =>
                $subwayLineList.insertAdjacentHTML(
                    "beforeend",
                    subwayLinesTemplate(line)))
            .catch(error => console.log(error));

        subwayLineModal.toggle();
    };

    const onDeleteSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (!isDeleteButton) {
            return;
        }

        const $deleteLineItem = $target.closest(".subway-line-item");
        api.line.delete($deleteLineItem.dataset.lineId)
            .then(() => $deleteLineItem.remove())
            .catch(error => console.log(error));
    };

    const onReadSubwayLineToUpdate = event => {
        const $target = event.target;
        const isUpdateButton = $target.classList.contains("mdi-pencil");
        if (!isUpdateButton) {
            return;
        }

        subwayLineModal.toggle();
        replaceEventListener(
            $submitSubwayLineButton,
            onCreateSubwayLine,
            onUpdateSubwayLine
        );

        $updateLineItem = $target.closest(".subway-line-item");
        const targetId = $updateLineItem.dataset.lineId;
        api.line.getById(targetId)
            .then(data => data.json())
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

        api.line.update(updatedSubwayLine, $updateLineItem.dataset.lineId)
            .then(data => data.json())
            .then(line => {
                $updateLineItem.insertAdjacentHTML(
                    "afterend",
                    subwayLinesTemplate(line)
                );
                $updateLineItem.remove();
                renderSubwayLineInfo(line);
            });

        subwayLineModal.toggle();
        replaceEventListener(
            $submitSubwayLineButton,
            onUpdateSubwayLine,
            onCreateSubwayLine
        );
    };

    const initDefaultSubwayLines = () => {
        api.line.get()
            .then(data => data.json())
            .then(lines =>
                lines.map(line => {
                    $subwayLineList.insertAdjacentHTML(
                        "beforeend",
                        subwayLinesTemplate(line)
                    );
                }));
    };

    const initEventListeners = () => {
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onShowSubwayLine);
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
