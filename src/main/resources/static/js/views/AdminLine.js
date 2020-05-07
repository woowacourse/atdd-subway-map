import {EVENT_TYPE} from "../../utils/constants.js";
import api from "../../api/index.js"
import {colorSelectOptionTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";

function AdminLine() {
    const $subwayLineList = document.querySelector("#subway-line-list");
    const $subwayLineNameInput = document.querySelector("#subway-line-name");
    const $subwayLineColorInput = document.querySelector("#subway-line-color");
    const $subwayFirstTime = document.querySelector("#first-time");
    const $subwayLastTime = document.querySelector("#last-time");
    const $subwayIntervalTime = document.querySelector("#interval-time");
    const $subwayFirstTimeInfo = document.querySelector("#first-time-info");
    const $subwayLastTimeInfo = document.querySelector("#last-time-info");
    const $subwayIntervalTimeInfo = document.querySelector("#interval-time-info");

    const $createSubwayLineButton = document.querySelector(
        "#subway-line-create-form #submit-button"
    );
    const subwayLineModal = new Modal();

    const onCreateSubwayLine = event => {
        event.preventDefault();
        const newSubwayLine = {
            title: $subwayLineNameInput.value,
            startTime: $subwayFirstTime.value,
            endTime: $subwayLastTime.value,
            intervalTime: $subwayIntervalTime.value,
            bgColor: $subwayLineColorInput.value
        };


        api.line.create(newSubwayLine)
            .then(data => {
                $subwayLineList.insertAdjacentHTML(
                    "beforeend",
                    subwayLinesTemplate(newSubwayLine)
                )
            })
            .catch(error => {
                const messages = [];
                for (let errorDto of error.body) {
                    messages.push(errorDto.message);
                }
                alert(messages.join("\n"));
            })
            .finally(() => {
                subwayLineModal.toggle();
                $subwayLineNameInput.value = "";
                $subwayLineColorInput.value = "";
            });
    };

    const onDeleteSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            event.preventDefault();
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

    const onSelectSubwayLine = event => {
        const $target = event.target;
        const isSelectText = $target.classList.contains("subway-line-text");
        if (isSelectText) {
            event.preventDefault();
            const subwayLineId = $target.closest("span").id;
            api.line.findById(subwayLineId).then(response => {
                const info = response.body;
                $subwayFirstTimeInfo.innerHTML = info.startTime;
                $subwayLastTimeInfo.innerHTML = info.endTime;
                $subwayIntervalTimeInfo.innerHTML = info.intervalTime + "분";
            })
        }
    };

    const onEditSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-pencil");
    };

    const initDefaultSubwayLines = () => {
        api.line.get().then(savedLines => savedLines.body.map(line => {
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
