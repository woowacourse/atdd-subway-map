import {EVENT_TYPE} from "../../utils/constants.js";
import {colorSelectOptionTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {defaultSubwayLines} from "../../utils/subwayMockData.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminLine() {
    let lines = [];
    const $subwayLineList = document.querySelector("#subway-line-list");
    const $subwayLineNameInput = document.querySelector("#subway-line-name");
    const $subwayFirstTimeInput = document.querySelector("#first-time");
    const $subwayLastTimeInput = document.querySelector("#last-time");
    const $subwayIntervalInput = document.querySelector("#interval-time");
    const $subwayLineColorInput = document.querySelector("#subway-line-color");

    const $createSubwayLineButton = document.querySelector(
        "#subway-line-create-form #submit-button"
    );
    const subwayLineModal = new Modal();

    const onCreateSubwayLine = async event => {
        event.preventDefault();
        const newSubwayLine = {
            name: $subwayLineNameInput.value,
            startTime: $subwayFirstTimeInput.value,
            endTime: $subwayLastTimeInput.value,
            intervalTime: $subwayIntervalInput.value,
            bgColor: $subwayLineColorInput.value
        };
        const line = await api.line.create(newSubwayLine);
        lines = [...lines, line];
        $subwayLineList.insertAdjacentHTML(
            "beforeend",
            subwayLinesTemplate(newSubwayLine)
        );
        subwayLineModal.toggle();
        $subwayLineNameInput.value = "";
        $subwayLineColorInput.value = "";
    };

    const onDeleteSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        const lineName = $target.closest('.subway-line-item').innerText.trim();
        if (isDeleteButton) {
            $target.closest(".subway-line-item").remove();
            const deleteLine = lines.find(line => line.name === lineName);
            api.line.delete(deleteLine.id);
            lines = lines.filter(line => line.name !== lineName);
        }
    };

    const onUpdateSubwayLine = event => {
        const $target = event.target;
        const isUpdateButton = $target.classList.contains("mdi-pencil");
        if (isUpdateButton) {
            subwayLineModal.toggle();
        }
    };

    const onEditSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-pencil");
    };

    const initDefaultSubwayLines = () => {
        defaultSubwayLines.map(line => {
            $subwayLineList.insertAdjacentHTML(
                "beforeend",
                subwayLinesTemplate(line)
            );
        });
    };

    const initEventListeners = () => {
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLine);
        $createSubwayLineButton.addEventListener(
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

    const showLines = async () => {
        const persistLines = await api.lines.get();
        lines = [...persistLines];
        persistLines.forEach(persistLine => $subwayLineList.insertAdjacentHTML(
            "beforeend",
            subwayLinesTemplate(persistLine)
        ));
    };

    this.init = () => {
        // initDefaultSubwayLines();
        showLines();
        initEventListeners();
        initCreateSubwayLineForm();
    };
}

const adminLine = new AdminLine();
adminLine.init();
