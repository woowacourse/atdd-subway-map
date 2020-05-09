import {EVENT_TYPE} from "../../utils/constants.js";
import api from "../../api/index.js";
import {colorSelectOptionTemplate, subwayLinesTemplate, timeTemplate} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";

function AdminLine() {
    const $subwayLineList = document.querySelector("#subway-line-list");
    const $subwayLineNameInput = document.querySelector("#subway-line-name");
    const $subwayStartTimeInput = document.querySelector("#first-time");
    const $subwayEndTimeInput = document.querySelector("#last-time");
    const $subwayIntervalTimeInput = document.querySelector("#interval-time");
    const $subwayLineBgColorInput = document.querySelector("#subway-line-color");
    const $subwayLineAddButton = document.querySelector("#subway-line-add-btn");
    const $subwayLineConfirmButton = document.querySelector("#subway-line-create-form #submit-button");
    const $closeModalButton = document.querySelector(".modal-close");
    const subwayLineModal = new Modal();

    let $selectedLine = null;
    let isUpdate = false;

    const updateLine = (updateLine) => {
        api.line
            .update($selectedLine.dataset.lineId, updateLine)
            .then(line => {
                $selectedLine.outerHTML = subwayLinesTemplate(line);
            });
    };

    const createLine = (newLine) => {
        api.line
            .create(newLine)
            .then(line => {
                $subwayLineList.insertAdjacentHTML(
                    "beforeend",
                    subwayLinesTemplate(line)
                );
            });
    };

    const onClickConfirm = event => {
        event.preventDefault();
        const line = {
            name: $subwayLineNameInput.value,
            startTime: $subwayStartTimeInput.value,
            endTime: $subwayEndTimeInput.value,
            intervalTime: $subwayIntervalTimeInput.value,
            bgColor: $subwayLineBgColorInput.value,
        };
        isUpdate ? updateLine(line) : createLine(line);
        subwayLineModal.toggle();
    };

    const onUpdateSubwayLine = event => {
        event.preventDefault();
        const $target = event.target;
        $selectedLine = $target.closest(".subway-line-item");
        const isUpdateButton = $target.classList.contains("mdi-pencil");
        if (isUpdateButton) {
            isUpdate = true;
            subwayLineModal.toggle();
            api.line
                .findBy($selectedLine.dataset.lineId)
                .then(line => {
                    $subwayLineNameInput.value = line.name;
                    $subwayStartTimeInput.value = line.startTime;
                    $subwayEndTimeInput.value = line.endTime;
                    $subwayIntervalTimeInput.value = line.intervalTime;
                    $subwayLineBgColorInput.value = line.bgColor;
                });
        }
    };

    const onDeleteSubwayLine = event => {
        event.preventDefault();
        const $target = event.target;
        const $parent = $target.closest(".subway-line-item");
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            api.line
                .delete($parent.dataset.lineId)
                .then(() => {
                    $target.closest(".subway-line-item").remove()
                });
        }
    };

    const onLoadSubwayLine = event => {
        event.preventDefault();
        const $target = event.target;
        if (!$target.classList.contains("subway-line-item")) {
            return;
        }
        api.line
            .findBy($target.dataset.lineId)
            .then(line => {
                const $lineInfo = document.querySelector("div.lines-info");
                line.startTime = line.startTime.slice(0, 5);
                line.endTime = line.endTime.slice(0, 5);
                $lineInfo.innerHTML = timeTemplate(line);
            });
    };

    const onCreateSubwayLine = event => {
        event.preventDefault();
        if (event.target.id === "subway-line-add-btn") {
            isUpdate = false;
        }
    };

    const initDefaultSubwayLines = () => {
        api.line
            .get()
            .then(lines => lines
                .map(line => {
                    $subwayLineList.insertAdjacentHTML(
                        "beforeend",
                        subwayLinesTemplate(line)
                    );
                })
            );
    };

    const clear = () => {
        $subwayLineNameInput.value = "";
        $subwayStartTimeInput.value = "";
        $subwayEndTimeInput.value = "";
        $subwayIntervalTimeInput.value = "";
        $subwayLineBgColorInput.value = "";
    };

    const initEventListeners = () => {
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onLoadSubwayLine);
        $subwayLineAddButton.addEventListener(EVENT_TYPE.CLICK, onCreateSubwayLine);
        $subwayLineConfirmButton.addEventListener(EVENT_TYPE.CLICK, onClickConfirm);
        $closeModalButton.addEventListener(EVENT_TYPE.CLICK, clear);
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
