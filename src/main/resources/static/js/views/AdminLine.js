import {EVENT_TYPE} from "../../utils/constants.js";
import {colorSelectOptionTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminLine() {
    const $subwayLineList = document.querySelector("#subway-line-list");
    const $subwayLineNameInput = document.querySelector("#subway-line-name");
    const $subwayLineStartTimeInput = document.querySelector("#first-time");
    const $subwayLineEndTimeInput = document.querySelector("#last-time");
    const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");
    const $subwayLineColorInput = document.querySelector("#subway-line-color");
    const $createSubwayLineButton = document.querySelector("#subway-line-create-form #submit-button");
    const $subwayLineStartTimeView = document.querySelector("#start-time-view");
    const $subwayLineEndTimeView = document.querySelector("#end-time-view");
    const $subwayLineIntervalTimeView = document.querySelector("#interval-time-view");

    const subwayLineModal = new Modal();

    let lineId = null;

    const onCreateSubwayLine = event => {
        event.preventDefault();

        const formData = {
            id: lineId,
            name: $subwayLineNameInput.value,
            startTime: document.querySelector("#first-time").value,
            endTime: document.querySelector("#last-time").value,
            intervalTime: document.querySelector("#interval-time").value,
            color: $subwayLineColorInput.value
        };

        lineId ? updateLineColumn(formData) : insertLineColumn(formData);

        subwayLineModal.toggle();
        $subwayLineNameInput.value = "";
        $subwayLineStartTimeInput.value = "";
        $subwayLineEndTimeInput.value = "";
        $subwayLineIntervalTimeInput.value = "";
        $subwayLineColorInput.value = "";
    };

    const insertLineColumn = formData => {
        api.line.create(formData).then(
            data => {
                $subwayLineList.insertAdjacentHTML(
                    "beforeend",
                    subwayLinesTemplate(data)
                );
            }
        );
    };

    const updateLineColumn = formData => {
        api.line.update(lineId, formData).then(
            data => {
                let oldLine = document.querySelector('div[data-id="' + lineId + '"]');
                oldLine.insertAdjacentHTML(
                    "afterend",
                    subwayLinesTemplate(data)
                );
                oldLine.remove();
                lineId = null;
            });
    };

    const onDeleteSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        lineId = $target.closest(".subway-line-item").dataset.id;
        if (isDeleteButton) {
            api.line.delete(lineId).then(response => {
                if (response.status === 204) {
                    $target.closest(".subway-line-item").remove();
                }
                lineId = null;
            });
        }
    };

    const onUpdateSubwayLine = event => {
        const $target = event.target;
        lineId = $target.closest(".subway-line-item").dataset.id;
        const isUpdateButton = $target.classList.contains("mdi-pencil");
        if (isUpdateButton) {
            subwayLineModal.toggle();
            api.line.find(lineId).then(data => {
                $subwayLineNameInput.value = data.name;
                $subwayLineStartTimeInput.value = data.startTime.substring(0,5);
                $subwayLineEndTimeInput.value = data.endTime.substring(0,5);
                $subwayLineIntervalTimeInput.value = data.intervalTime;
                $subwayLineColorInput.value = data.color;
            });
        }
    };

    const onEditSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-pencil");
    };

    const onSelectSubwayLine = event => {
        const $target = event.target;
        const id = $target.closest(".subway-line-item").dataset.id;
        const isLineName = $target.id === "line-name";
        if(isLineName) {
            api.line.find(id).then(data => {
                $subwayLineStartTimeView.innerHTML = data.startTime.substring(0,5);
                $subwayLineEndTimeView.innerHTML = data.endTime.substring(0,5);
                $subwayLineIntervalTimeView.innerHTML = data.intervalTime.toString()+"분";
            })

        }
    };

    const initDefaultSubwayLines = () => {
        api.line.get().then(data =>
            data.map(line => {
                $subwayLineList.insertAdjacentHTML(
                    "beforeend",
                    subwayLinesTemplate(line)
                );
            }));
    };

    const initEventListeners = () => {
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onSelectSubwayLine);
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

    this.init = () => {
        initDefaultSubwayLines();
        initEventListeners();
        initCreateSubwayLineForm();
    };
}

const adminLine = new AdminLine();
adminLine.init();
