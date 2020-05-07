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

    const onViewSubwayInfo = event => {
        const $target = event.target;
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

    const onCreateSubwayLine = event => {
        event.preventDefault();

        const data = {
            name: $subwayLineNameInput.value,
            startTime: $subwayLineFirstTimeInput.value,
            endTime: $subwayLineLastTimeInput.value,
            intervalTime: $subwayLineIntervalTimeInput.value,
            bgColor: $subwayLineColorInput.value
        };
        api.line.create(data)
            .then(() => api.line.get().then(subwayLines => subwayLines.forEach(
                subwayLine => {
                    $subwayLineList.insertAdjacentHTML(
                        "beforeend",
                        subwayLinesTemplate(subwayLine)
                    );
                }
            )));

        subwayLineModal.toggle();
        $subwayLineNameInput.value = "";
        $subwayLineColorInput.value = "";
    };

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
            subwayLineModal.toggle();
        }
    };

    const onEditSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-pencil");
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
