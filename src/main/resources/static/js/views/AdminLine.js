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
                })
                .catch(error => {
                    alert("에러가 발생했다");
                });
        }
        subwayLineModal.toggle();
        initLineModal();
    };

    function fillLineModal(id, name, color, start, end, interval) {
        $subwayLineIdInput.innerText = id;
        $subwayLineNameInput.value = name;
        $subwayLineColorInput.value = color;
        $subwayLineStartTimeInput.value = start;
        $subwayLineEndTimeInput.value = end;
        $subwayLineIntervalTimeInput.value = interval;
    }

    function initLineModal() {
        fillLineModal("", "", "", "", "", "");
    }

    function fillLineInformation(start, end, interval) {
        const $subwayLineStartTime = document.querySelector("#line-start-time");
        const $subwayLineEndTime = document.querySelector("#line-end-time");
        const $subwayLineIntervalTime = document.querySelector("#line-interval-time");

        $subwayLineStartTime.innerText = start;
        $subwayLineEndTime.innerText = end;
        $subwayLineIntervalTime.innerText = interval;

    }

    const onDeleteSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            let selectedLine = $target.closest(".subway-line-item");
            let selectedLineId = selectedLine.querySelector(".line-id").innerText;
            api.line.delete(selectedLineId);
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
                .then(response => fillLineModal(response.id, response.name, response.bgColor, response.startTime, response.endTime, response.intervalTime));
            subwayLineModal.toggle();
        }
    };


    const onShowSubwayInformation = event => {
        const $target = event.target;

        const isUpdateButton = $target.classList.contains("mdi-pencil");
        const isDeleteButton = $target.classList.contains("mdi-delete");

        if (!isUpdateButton && !isDeleteButton) {
            let selectedLine = $target.closest(".subway-line-item");
            let selectedLineId = selectedLine.querySelector(".line-id").innerText;

            api.line.getLine(selectedLineId)
                .then(response => fillLineInformation(response.startTime, response.endTime, response.intervalTime));


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
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onShowSubwayInformation);
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