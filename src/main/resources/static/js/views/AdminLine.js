import {EVENT_TYPE} from "../../utils/constants.js";
import {
    subwayLinesTemplate,
    colorSelectOptionTemplate
} from "../../utils/templates.js";
import {defaultSubwayLines} from "../../utils/subwayMockData.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";

function AdminLine() {
    const $subwayLineList = document.querySelector("#subway-line-list");
    const $subwayLineNameInput = document.querySelector("#subway-line-name");
    const $subwayLineColorInput = document.querySelector("#subway-line-color");
    const $subwayLineFirstTimeInput = document.querySelector("#first-time");
    const $subwayLineLastTimeInput = document.querySelector("#last-time");
    const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");
    const $subwayLineFirstTimeInfo = document.querySelector("#first-time-info");
    const $subwayLineLastTimeInfo = document.querySelector("#last-time-info");
    const $subwayLineIntervalTimeInfo = document.querySelector("#interval-time-info");

    const $createSubwayLineButton = document.querySelector(
        "#subway-line-create-form #submit-button"
    );
    const subwayLineModal = new Modal();

    const onCreateSubwayLine = event => {
        event.preventDefault();
        const newSubwayLine = {
            name: $subwayLineNameInput.value,
            color: $subwayLineColorInput.value,
            startTime: $subwayLineFirstTimeInput.value,
            endTime: $subwayLineLastTimeInput.value,
            intervalTime: $subwayLineIntervalTimeInput.value
        };

        sendNewLine(newSubwayLine).then(() => location.reload());

        $subwayLineList.insertAdjacentHTML(
            "beforeend",
            subwayLinesTemplate({
                title: newSubwayLine.name,
                bgColor: newSubwayLine.color
            })
        );
        subwayLineModal.toggle();
        $subwayLineNameInput.value = "";
        $subwayLineColorInput.value = "";
    };

    const sendNewLine = data => {
        return fetch("/lines", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).catch(err => console.log(err));
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

    const onDetailSubwayLine = async event => {
        event.preventDefault();
        if (event.target && event.target.classList.contains("subway-line-item")) {
            const $id = event.target.querySelector('input');
            const line = await getLine($id.value);
            $subwayLineFirstTimeInfo.innerText = line.startTime;
            $subwayLineLastTimeInfo.innerText = line.endTime;
            $subwayLineIntervalTimeInfo.innerText = line.intervalTime;
        }
    }

    const getLine = (id) => {
        return fetch(`/lines/${id}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).then(res => res.json());
    }

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
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDetailSubwayLine);
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
        // initDefaultSubwayLines();
        initEventListeners();
        initCreateSubwayLineForm();
    };
}

const adminLine = new AdminLine();
adminLine.init();
