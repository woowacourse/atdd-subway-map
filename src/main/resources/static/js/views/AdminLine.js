import {EVENT_TYPE} from "../../utils/constants.js";
import {colorSelectOptionTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js"

function AdminLine() {
    const $subwayLineList = document.querySelector("#subway-line-list");
    const $subwayLineIdInput = document.querySelector("#subway-line-id");
    const $subwayLineNameInput = document.querySelector("#subway-line-name");
    const $subwayLineColorInput = document.querySelector("#subway-line-color");
    const $subwayLineStartTimeInput = document.querySelector("#first-time");
    const $subwayLineEndTimeInput = document.querySelector("#last-time");
    const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");

    const $createSubwayLineButton = document.querySelector("#subway-line-create-form #submit-button");

    const subwayLineModal = new Modal();

    const onCreateSubwayLine = event => {
        event.preventDefault();

        const newSubwayLine = {
            name: $subwayLineNameInput.value,
            bgColor: $subwayLineColorInput.value,
            startTime: $subwayLineStartTimeInput.value,
            endTime: $subwayLineEndTimeInput.value,
            intervalTime: $subwayLineIntervalTimeInput.value
        };

        const updateSubwayLine = {
            startTime: $subwayLineStartTimeInput.value,
            endTime: $subwayLineEndTimeInput.value,
            intervalTime: $subwayLineIntervalTimeInput.value
        };


        if ($subwayLineIdInput.innerText !== "") {
            let result = api.line.update(updateSubwayLine, $subwayLineIdInput.innerText);
        } else {
            let result = api.line.create(newSubwayLine)
                .then(response => {
                    $subwayLineList.insertAdjacentHTML(
                        "beforeend",
                        subwayLinesTemplate(response)
                    );
                });
        }
        subwayLineModal.toggle();
        initLineInformation();
    };

    function fillLineInformation(id, name, color, start, end, interval) {
        $subwayLineIdInput.innerText = id;
        $subwayLineNameInput.value = name;
        $subwayLineColorInput.value = color;
        $subwayLineStartTimeInput.value = start;
        $subwayLineEndTimeInput.value = end;
        $subwayLineIntervalTimeInput.value = interval;
    }

    function initLineInformation() {
        fillLineInformation("", "", "", "", "", "");
    }

    const onDeleteSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            $target.closest(".subway-line-item").remove();
        }
    };

    const onUpdateSubwayLine = event => {
        const $target = event.target;
        const isUpdateButton = $target.classList.contains("mdi-pencil");
        if (isUpdateButton) {
            //기존 데이터 보여주기
            let selectedLine = $target.closest(".subway-line-item");
            let selectedLineId = selectedLine.querySelector(".line-id").innerText;

            api.line.getLine(selectedLineId)
                .then(response => fillLineInformation(response.id, response.name, response.bgColor, response.startTime, response.endTime, response.intervalTime));
            subwayLineModal.toggle();
        }
    };

    const onEditSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-pencil");
    };

    const initDefaultSubwayLines = () => {
        api.line.getLines()
            .then(response => {
                response.forEach(line => $subwayLineList.insertAdjacentHTML(
                    "beforeend",
                    subwayLinesTemplate(line)
                ));
            });
    };

    const initEventListeners = () => {
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLine);
        $createSubwayLineButton.addEventListener(EVENT_TYPE.CLICK, onCreateSubwayLine);
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
        $colorSelectContainer.addEventListener(EVENT_TYPE.CLICK, onSelectColorHandler);
    };

    this.init = () => {
        initDefaultSubwayLines();
        initEventListeners();
        initCreateSubwayLineForm();
    };
}

const adminLine = new AdminLine();
adminLine.init();