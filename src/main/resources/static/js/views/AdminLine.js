import {EVENT_TYPE} from "../../utils/constants.js";
import api from "../../api/index.js";
import {colorSelectOptionTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";

function AdminLine() {
    const $subwayLineList = document.querySelector("#subway-line-list");
    const $subwayLineNameInput = document.querySelector("#subway-line-name");
    const $subwayLineFirstTimeInput = document.querySelector("#first-time");
    const $subwayLineLastTimeInput = document.querySelector("#last-time");
    const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");
    const $subwayLineColorInput = document.querySelector("#subway-line-color");
    const $subwayLineId = document.querySelector("#subway-line-id");

    const $createSubwayLineButton = document.querySelector(
        "#subway-line-create-form #submit-button"
    );
    const $closeSubwayLineButton = document.querySelector(
        "#subway-line-create-form .modal-close"
    );

    const subwayLineModal = new Modal();

    const checkCreateOrUpdate = async event => {
        event.preventDefault();
        const newSubwayLine = {
            name: $subwayLineNameInput.value,
            startTime: $subwayLineFirstTimeInput.value,
            endTime: $subwayLineLastTimeInput.value,
            intervalTime: $subwayLineIntervalTimeInput.value,
            backgroundColor: $subwayLineColorInput.value
        };
        if ($subwayLineId.value === "") {
            await onCreateSubwayLine(newSubwayLine);
        } else {
            await onUpdateSubwayLine(newSubwayLine);
        }
        subwayLineModal.toggle();
        onEmptyInput();
    };

    const onCreateSubwayLine = (newSubwayLine) => {
        return api.lines.create(newSubwayLine).then(data => {
            $subwayLineList.insertAdjacentHTML(
                "beforeend",
                subwayLinesTemplate(data)
            );
        }).catch(error => {
            alert(error.message);
        });
    }

    const onUpdateSubwayLine = (newSubwayLine) => {
        return api.lines.update($subwayLineId.value, newSubwayLine).then(data => {
            let standardNode = document.querySelector(`[data-line-id="${$subwayLineId.value}"]`);
            let divNode = document.createElement("div");
            divNode.innerHTML = subwayLinesTemplate(data);
            $subwayLineList.insertBefore(divNode.firstChild, standardNode);
            standardNode.remove();
        }).catch(error => {
            alert(error.message);
        })
    }

    const onSelectSubwayLine = event => {
        const $target = event.target;
        const isSubwayLine = $target.classList.contains("subway-line-item");
        if (isSubwayLine) {
            api.lines.find($target.dataset.lineId).then(data => {
                document.querySelector("#line-info-first-time").innerText = data.startTime.substring(0, 5);
                document.querySelector("#line-info-last-time").innerText = data.endTime.substring(0, 5);
                document.querySelector("#line-info-interval-time").innerText = data.intervalTime;
            })
        }
    }

    const onDeleteSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            $target.closest(".subway-line-item").remove();
            let subwayLineId = $target.closest(".subway-line-item").dataset.lineId;
            api.lines.delete(subwayLineId);
        }
    };

    const onEditSubwayLine = event => {
        const $target = event.target;
        const isUpdateButton = $target.classList.contains("mdi-pencil");
        if (isUpdateButton) {
            let subwayLineId = $target.closest(".subway-line-item").dataset.lineId;
            api.lines.find(subwayLineId).then(data => {
                $subwayLineId.value = data.id;
                $subwayLineNameInput.value = data.name;
                $subwayLineFirstTimeInput.value = data.startTime;
                $subwayLineLastTimeInput.value = data.endTime;
                $subwayLineIntervalTimeInput.value = data.intervalTime;
                $subwayLineColorInput.value = data.backgroundColor;
            })
            subwayLineModal.toggle();
        }
        onEmptyInput();
    };

    const onEmptyInput = () => {
        $subwayLineId.value = "";
        $subwayLineNameInput.value = "";
        $subwayLineFirstTimeInput.value = "";
        $subwayLineLastTimeInput.value = "";
        $subwayLineIntervalTimeInput.value = "";
        $subwayLineColorInput.value = "";
    }

    const initDefaultSubwayLines = () => {
        api.lines.get().then(data => {
            data.map(line => {
                    $subwayLineList.insertAdjacentHTML(
                        "beforeend",
                        subwayLinesTemplate(line)
                    );
                }
            )
        });
    };

    const initEventListeners = () => {
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onEditSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onSelectSubwayLine);

        $createSubwayLineButton.addEventListener(
            EVENT_TYPE.CLICK,
            checkCreateOrUpdate
        );
        $closeSubwayLineButton.addEventListener(
            EVENT_TYPE.CLICK,
            onEmptyInput
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
