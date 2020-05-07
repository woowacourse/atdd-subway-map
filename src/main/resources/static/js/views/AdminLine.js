import {EVENT_TYPE} from "../../utils/constants.js";
import api from "../../api/index.js";
import {colorSelectOptionTemplate, subwayLinesTemplate, timeTemplate} from "../../utils/templates.js";
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
        api.line
            .create(req)
            .then(line => {
                $subwayLineList.insertAdjacentHTML(
                    "beforeend",
                    subwayLinesTemplate(line)
                );
                subwayLineModal.toggle();
                $subwayLineNameInput.value = "";
                $subwayFirstTimeInput.value = "";
                $subwayLastTimeInput.value = "";
                $subwayIntervalTimeInput.value = "";
                $subwayLineColorInput.value = "";
            });
    };

    const onDeleteSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            $target.closest(".subway-line-item").remove();
        }
    };

    const onUpdateSubwayLine = event => {
        event.preventDefault();
        const $target = event.target;
        const $parent = $target.parentElement;
        const isUpdateButton = $target.classList.contains("mdi-pencil");
        if (isUpdateButton) {
            subwayLineModal.toggle();
            api.line
                .findBy($parent.id)
                .then(line => {
                    $subwayLineNameInput.value = line.name;
                    $subwayFirstTimeInput.value = line.startTime;
                    $subwayLastTimeInput.value = line.endTime;
                    $subwayIntervalTimeInput.value = line.intervalTime;
                    $subwayLineColorInput.value = line.bgColor;
                });

        }
    };

    const onEditSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-pencil");
    };

    const onLoadSubwayLine = event => {
        const $target = event.target;
        if (!$target.classList.contains("subway-line-item")) {
            return;
        }
        api.line.findBy($target.id)
            .then(line => {
                const $lineInfo = document.querySelector("div.lines-info");
                $lineInfo.innerHTML = timeTemplate(line);
            });
    };

    const initDefaultSubwayLines = () => {
        api.line
            .get()
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
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onLoadSubwayLine);
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
