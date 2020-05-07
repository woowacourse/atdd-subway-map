import {EVENT_TYPE} from "../../utils/constants.js";
import {
    subwayLinesTemplate,
    colorSelectOptionTemplate
} from "../../utils/templates.js";
import {defaultSubwayLines} from "../../utils/subwayMockData.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";

function AdminLine() {
    // list
    const $subwayLineList = document.querySelector("#subway-line-list");

    // detail
    const $subwayLineFirstTimeInfo = document.querySelector("#first-time-info");
    const $subwayLineLastTimeInfo = document.querySelector("#last-time-info");
    const $subwayLineIntervalTimeInfo = document.querySelector("#interval-time-info");

    // create & update modal
    const $subwayLineNameInput = document.querySelector("#subway-line-name");
    const $subwayLineColorInput = document.querySelector("#subway-line-color");
    const $subwayLineFirstTimeInput = document.querySelector("#first-time");
    const $subwayLineLastTimeInput = document.querySelector("#last-time");
    const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");
    const $subwayLineUpdateId = document.querySelector("#update-line-id");
    const $submitSubwayLineButton = document.querySelector("#submit-button");
    const $cancelSubwayLineButton = document.querySelector("#cancel-button");

    let $currentSubwayLine;
    let isEdit = false;

    const subwayLineModal = new Modal();

    const onSubmitSubwayLine = event => {
        event.preventDefault();
        let newSubwayLine = {
            name: $subwayLineNameInput.value,
            color: $subwayLineColorInput.value,
            startTime: $subwayLineFirstTimeInput.value,
            endTime: $subwayLineLastTimeInput.value,
            intervalTime: $subwayLineIntervalTimeInput.value
        };
        if (isEdit) {
            newSubwayLine.id = $subwayLineUpdateId.value;
        }

        sendNewLine(newSubwayLine).then(() => location.reload());

        $subwayLineList.removeChild($currentSubwayLine);
        $subwayLineList.insertAdjacentHTML(
            "beforeend",
            subwayLinesTemplate({
                title: newSubwayLine.name,
                bgColor: newSubwayLine.color
            })
        );
        subwayLineModal.toggle();
        clearForm();
    };

    const sendNewLine = data => {
        if (isEdit) {
            return fetch(`/lines/${data.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            }).catch(err => console.log(err));
        }
        return fetch("/lines", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).catch(err => console.log(err));
    };

    const clearForm = () => {
        $subwayLineFirstTimeInput.value = "";
        $subwayLineLastTimeInput.value = "";
        $subwayLineIntervalTimeInput.value = "";
        $subwayLineUpdateId.value = "";
        $subwayLineNameInput.value = "";
        $subwayLineColorInput.value = "";
    }

    const onDeleteSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            $target.closest(".subway-line-item").remove();
        }
    };

    const onEditSubwayLine = async event => {
        event.preventDefault();
        if (event.target && event.target.classList.contains("mdi-pencil")) {
            isEdit = true;
            $currentSubwayLine = event.target.closest("div");
            const $id = $currentSubwayLine.querySelector('input');
            const line = await getLine($id.value);
            $subwayLineUpdateId.value = line.id;
            $subwayLineNameInput.value = line.name;
            $subwayLineColorInput.value = line.color;
            $subwayLineFirstTimeInput.value = line.startTime;
            $subwayLineLastTimeInput.value = line.endTime;
            $subwayLineIntervalTimeInput.value = line.intervalTime;
            subwayLineModal.toggle();
        }
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

    const onCancelSubwayLine = () => {
        clearForm();
    }

    const initEventListeners = () => {
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onEditSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDetailSubwayLine);
        $submitSubwayLineButton.addEventListener(EVENT_TYPE.CLICK, onSubmitSubwayLine);
        $cancelSubwayLineButton.addEventListener(EVENT_TYPE.CLICK, onCancelSubwayLine);
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
