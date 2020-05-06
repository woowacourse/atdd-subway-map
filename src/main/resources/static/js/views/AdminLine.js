import {EVENT_TYPE} from "../../utils/constants.js";
import {colorSelectOptionTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";

function AdminLine() {
    const $subwayLineList = document.querySelector("#subway-line-list");
    const $subwayLineNameInput = document.querySelector("#subway-line-name");
    const $subwayFirstTimeInput = document.querySelector("#first-time");
    const $subwayLastTimeInput = document.querySelector("#last-time");
    const $subwayIntervalTimeInput = document.querySelector("#interval-time");
    const $subwayLineColorInput = document.querySelector("#subway-line-color");

    const $createSubwayLineButton = document.querySelector(
        "#subway-line-create-form #submit-button"
    );
    const subwayLineModal = new Modal();

    const onCreateSubwayLine = event => {
        event.preventDefault();
        const req = {
            name: $subwayLineNameInput.value,
            startTime: $subwayFirstTimeInput.value,
            endTime: $subwayLastTimeInput.value,
            intervalTime: $subwayIntervalTimeInput.value,
            bgColor: $subwayLineColorInput.value
        };
        fetch("/lines", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(req)
        }).then(res => {
            if (res.ok) {
                const newSubwayLine = {
                    name: $subwayLineNameInput.value,
                    bgColor: $subwayLineColorInput.value
                };
                $subwayLineList.insertAdjacentHTML(
                    "beforeend",
                    subwayLinesTemplate(newSubwayLine)
                );
                subwayLineModal.toggle();
                $subwayLineNameInput.value = "";
                $subwayFirstTimeInput.value = "";
                $subwayLastTimeInput.value = "";
                $subwayIntervalTimeInput.value = "";
                $subwayLineColorInput.value = "";
            }
        })
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
        fetch("/lines")
            .then(response => response.json())
            .then(lines => lines.map(line => {
                    $subwayLineList.insertAdjacentHTML(
                        "beforeend",
                        subwayLinesTemplate(line)
                    );
                })
            );
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

    this.init = () => {
        initDefaultSubwayLines();
        initEventListeners();
        initCreateSubwayLineForm();
    };
}

const adminLine = new AdminLine();
adminLine.init();
