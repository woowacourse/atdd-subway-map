import {EVENT_TYPE} from "../../utils/constants.js";
import {
    subwayLinesTemplate,
    colorSelectOptionTemplate
} from "../../utils/templates.js";
import {defaultSubwayLines} from "../../utils/subwayMockData.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";

// TODO: validate 구현한 파일 분리
// TODO: fetch 파일 분리
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
    const $subwayLineUpdateId = document.querySelector("#line-update-id");
    const $submitSubwayLineButton = document.querySelector("#submit-button");
    const $cancelSubwayLineButton = document.querySelector("#cancel-button");

    let $currentSubwayLineItem;
    let isEdit = false;

    const subwayLineModal = new Modal();

    const onSubmitSubwayLine = event => {
        event.preventDefault();
        let inputSubwayLine = {
            name: $subwayLineNameInput.value,
            color: $subwayLineColorInput.value,
            startTime: $subwayLineFirstTimeInput.value,
            endTime: $subwayLineLastTimeInput.value,
            intervalTime: $subwayLineIntervalTimeInput.value
        };
        if (isEdit) {
            inputSubwayLine.id = $subwayLineUpdateId.value;
        }

        // validateSubwayLine(inputSubwayLine);
        sendNewLine(inputSubwayLine).then(() => location.reload());

        const newLineTemplate = subwayLinesTemplate({
            title: inputSubwayLine.name,
            bgColor: inputSubwayLine.color
        });

        const $subwayLineItem = document.createElement('div');
        $subwayLineItem.innerHTML = newLineTemplate;

        if (isEdit) {
            $subwayLineList.replaceChild($subwayLineItem.firstChild, $currentSubwayLineItem);
        } else {
            $subwayLineList.insertAdjacentHTML("beforeend", newLineTemplate);
        }
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
        if (event.target && event.target.classList.contains("mdi-delete")) {
            event.target.closest(".subway-line-item").remove();
            const id = event.target.closest("div").querySelector('input').value;

            fetch(`lines/${id}`, {
                method: "DELETE"
            });
        }
    };

    const onEditSubwayLine = async event => {
        event.preventDefault();
        if (event.target && event.target.classList.contains("mdi-pencil")) {
            isEdit = true;
            $currentSubwayLineItem = event.target.closest("div");
            const $id = $currentSubwayLineItem.querySelector('input');
            //const $id = $currentSubwayLineItem.dataset.lineId;
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
