import {ERROR_MESSAGE, EVENT_TYPE} from "../../utils/constants.js";
import {colorSelectOptionTemplate, subwayLineInfoTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminLine() {
    const $subwayLineList = document.querySelector("#subway-line-list");
    const $subwayLineNameInput = document.querySelector("#subway-line-name");
    const $subwayLineStartTimeInput = document.querySelector("#subway-first-time");
    const $subwayLineEndTimeInput = document.querySelector("#subway-last-time");
    const $subwayLineIntervalTimeInput = document.querySelector("#subway-interval-time");
    const $subwayLineColorInput = document.querySelector("#subway-line-color");
    const $subwayLineIdInput = document.querySelector("#subway-line-id");

    const $createSubwayLineButton = document.querySelector(
        "#subway-line-create-form #submit-button"
    );
    const subwayLineModal = new Modal();

    const clearModal = event => {
        $subwayLineNameInput.value = "";
        $subwayLineStartTimeInput.value = "";
        $subwayLineEndTimeInput.value = "";
        $subwayLineIntervalTimeInput.value = "";
        $subwayLineColorInput.value = "";
    }

    const onCreateSubwayLine = event => {
        const $target = event.target;
        const isCreateButton = $target.classList.contains("create-btn");
        if (isCreateButton && validateLineTitle()) {
            const newSubwayLine = {
                title: $subwayLineNameInput.value,
                startTime: $subwayLineStartTimeInput.value,
                endTime: $subwayLineEndTimeInput.value,
                intervalTime: $subwayLineIntervalTimeInput.value,
                bgColor: $subwayLineColorInput.value
            };
            $subwayLineList.insertAdjacentHTML(
                "beforeend",
                subwayLinesTemplate(newSubwayLine)
            );
            api.line.create(newSubwayLine);
            subwayLineModal.toggle();
            subwayLineModal.toggleCreateButton();
        }
    };

    const onDeleteSubwayLine = async event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            const $targetLine = $target.parentNode.parentNode;
            const targetLineTitle = $targetLine.textContent.trim();
            $target.closest(".subway-line-item").remove();
            const lineByTitle = await findSubwayLineByTitle(targetLineTitle);
            if (lineByTitle !== null) {
                await api.line.delete(lineByTitle.id);
            }
        }
    };

    const onUpdateSubwayLine = async event => {
        const $target = event.target;
        const isEditButton = $target.classList.contains("mdi-pencil");
        if (isEditButton) {
            subwayLineModal.toggle();
            const $targetLine = $target.parentNode.parentNode;
            const lines = await api.line.get();
            lines.map(line => {
                if (line.title === $targetLine.textContent.trim()) {
                    $subwayLineIdInput.value = line.id;
                    $subwayLineNameInput.value = line.title;
                    $subwayLineStartTimeInput.value = line.startTime.slice(0, -3);
                    $subwayLineEndTimeInput.value = line.endTime.slice(0, -3);
                    $subwayLineIntervalTimeInput.value = line.intervalTime;
                    $subwayLineColorInput.value = line.bgColor;
                }
            });
        }
    };

    const onEditSubwayLine = async event => {
        const $target = event.target;
        const isEditButton = !$target.classList.contains("create-btn");
        if (isEditButton && validateLineTitle()) {
            const editSubwayLine = {
                title: $subwayLineNameInput.value,
                startTime: $subwayLineStartTimeInput.value,
                endTime: $subwayLineEndTimeInput.value,
                intervalTime: $subwayLineIntervalTimeInput.value,
                bgColor: $subwayLineColorInput.value
            };
            await api.line.update(editSubwayLine, $subwayLineIdInput.value);
        }
    };

    const showSubwayLineInfo = async event => {
        const $target = event.target;
        const $subwayLinesInfo = document.querySelector(".lines-info");
        const isShowButton = $target.classList.contains("subway-line-item");
        const targetLineTitle = $target.textContent.trim()
        if (isShowButton) {
            const lineByTitle = await findSubwayLineByTitle(targetLineTitle);
            if (lineByTitle !== null) {
                $subwayLinesInfo.innerHTML = subwayLineInfoTemplate(lineByTitle);
            }
        }
    };

    const findSubwayLineByTitle = async title => {
        const lines = await api.line.get();
        return lines.find(line => line.title === title);
    }

    const initSubwayLines = async () => {
        const lines = await api.line.get();
        lines.map(line => {
            $subwayLineList.insertAdjacentHTML(
                "beforeend",
                subwayLinesTemplate(line)
            );
        })
    }

    const getSubwayLines = () => {
        return Array.from($subwayLineList.childNodes)
            .map(x => x.textContent)
            .map(x => x.trim());
    }

    const validateLineTitle = () => {
        if (getSubwayLines().includes($subwayLineNameInput.value)) {
            alert(ERROR_MESSAGE.NO_DUPLICATED);
            $subwayLineNameInput.value = "";
            return false;
        }
        if (!$subwayLineNameInput.value) {
            alert(ERROR_MESSAGE.NOT_EMPTY);
            return false;
        }
        return true;
    }

    const initEventListeners = () => {
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, showSubwayLineInfo);
        $createSubwayLineButton.addEventListener(EVENT_TYPE.CLICK, onEditSubwayLine);
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
        $colorSelectContainer.addEventListener(
            EVENT_TYPE.CLICK,
            onSelectColorHandler
        );
    };

    this.init = () => {
        initSubwayLines();
        initEventListeners();
        initCreateSubwayLineForm();
    };
}

const adminLine = new AdminLine();
adminLine.init();
