import {EVENT_TYPE} from "../../utils/constants.js";
import {
    colorSelectOptionTemplate,
    subwayLinesTemplate
} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import api from "../../api/index.js";
import Modal from "../../ui/Modal.js";

function AdminLine() {
    const $subwayLineList = document.querySelector("#subway-line-list");
    const $subwayLineNameInput = document.querySelector("#subway-line-name");
    const $subwayLineColorInput = document.querySelector("#subway-line-color");
    const $subwayLineFirstTime = document.querySelector("#first-time");
    const $subwayLineLastTime = document.querySelector("#last-time");
    const $subwayLineIntervalTime = document.querySelector("#interval-time");
    const $viewStartTime = document.querySelector("#view-start-time");
    const $viewEndTime = document.querySelector("#view-end-time");
    const $viewIntervalTime = document.querySelector("#view-interval-time");
    const $subwayLineId = document.querySelector("#lineId");

    const $createSubwayLineButton = document.querySelector(
        "#subway-line-create-form #submit-button"
    );
    const subwayLineModal = new Modal();

    function addSubwayLineList(newSubwayLine) {
        $subwayLineList.insertAdjacentHTML(
            "beforeend",
            subwayLinesTemplate(newSubwayLine)
        );
    }

    function removeSubwayLineList(id) {
        let selectId = "#line-" + id;
        $subwayLineList.removeChild(document.querySelector(selectId));
    }

    function onCreateSubwayLine() {
        let newSubwayLine = {
            name: $subwayLineNameInput.value,
            color: $subwayLineColorInput.value,
            startTime: $subwayLineFirstTime.value,
            endTime: $subwayLineLastTime.value,
            intervalTime: $subwayLineIntervalTime.value
        };

        api.line.create(newSubwayLine).then(res => {
            if (res.status !== 201) {
                return;
            }
            return res.json();
        }).then(res => {
            if (res === undefined) {
                $viewStartTime.innerHTML = "";
                $viewEndTime.innerHTML = "";
                $viewIntervalTime.innerHTML = "";
                return;
            }
            newSubwayLine['id'] = res.id;
            addSubwayLineList(newSubwayLine);
            $viewStartTime.innerHTML = newSubwayLine.startTime;
            $viewEndTime.innerHTML = newSubwayLine.endTime;
            $viewIntervalTime.innerHTML = newSubwayLine.intervalTime + "분";
        });

        subwayLineModal.toggle();
        $subwayLineNameInput.value = "";
        $subwayLineColorInput.value = "";
        $subwayLineFirstTime.value = "";
        $subwayLineLastTime.value = "";
        $subwayLineIntervalTime.value = "";
        $subwayLineId.value = "";
    };

    const onDeleteSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            const lineId = $target.closest(".subway-line-item").id.split(
                "-")[1];
            api.line.delete("/" + lineId).then(response => {
                if (response.status !== 200) {
                    alert("삭제불가!");
                    throw new Error("HTTP status " + response.status);
                }
            }).then(() => $target.closest(".subway-line-item").remove());
        }
    };

    const onEditToggle = event => {
        const $target = event.target;
        const isUpdateButton = $target.classList.contains("mdi-pencil");
        if (isUpdateButton) {
            const lineId = $target.closest(".subway-line-item").id.split(
                "-")[1];
            api.line.get('/' + lineId).then(res => {
                    $subwayLineNameInput.value = res.name;
                    $subwayLineColorInput.value = res.color;
                    $subwayLineFirstTime.value = res.startTime;
                    $subwayLineLastTime.value = res.endTime;
                    $subwayLineIntervalTime.value = res.intervalTime;
                    $subwayLineId.value = res.id;
                    subwayLineModal.toggle();
                }
            )
        }
    };

    const onSelectSubwayLine = event => {
        const $target = event.target;
        const isSelectSubwayLine
            = $target.classList.contains("subway-line-item");
        if (isSelectSubwayLine) {
            api.line.get('/' + $target.id).then(line => {
                    $viewStartTime.innerHTML = line.startTime.slice(0, 5);
                    $viewEndTime.innerHTML = line.endTime.slice(0, 5);
                    $viewIntervalTime.innerHTML = line.intervalTime + "분";
                }
            )
        }
    };

    const onEditSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-pencil");
    };

    const initDefaultSubwayLines = () => {
        api.line.get().then(newSubwayLines => {
            newSubwayLines.forEach(newSubwayLine => {
                addSubwayLineList(newSubwayLine)
            })
        });
    };

    const initEventListeners = () => {
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onEditToggle);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onSelectSubwayLine);
        $createSubwayLineButton.addEventListener(
            EVENT_TYPE.CLICK,
            createOrUpdate
        );
    };

    const createOrUpdate = event => {
        event.preventDefault();

        if ($subwayLineId.value === "") {
            onCreateSubwayLine();
            return;
        }
        onUpdateSubwayLine();
    };

    function onUpdateSubwayLine() {
        let newSubwayLine = {
            name: $subwayLineNameInput.value,
            color: $subwayLineColorInput.value,
            startTime: $subwayLineFirstTime.value,
            endTime: $subwayLineLastTime.value,
            intervalTime: $subwayLineIntervalTime.value
        };

        api.line.update("/" + $subwayLineId.value, newSubwayLine).then(res => {
            if (res.status !== 200) {
                return;
            }
            return res.json();
        }).then(res => {
            if (res === undefined) {
                $viewStartTime.innerHTML = "";
                $viewEndTime.innerHTML = "";
                $viewIntervalTime.innerHTML = "";
                return;
            }
            newSubwayLine['id'] = res.id;
            removeSubwayLineList(res.id);
            addSubwayLineList(newSubwayLine);
            $viewStartTime.innerHTML = newSubwayLine.startTime;
            $viewEndTime.innerHTML = newSubwayLine.endTime;
            $viewIntervalTime.innerHTML = newSubwayLine.intervalTime + "분";
        });

        subwayLineModal.toggle();
        $subwayLineNameInput.value = "";
        $subwayLineColorInput.value = "";
        $subwayLineFirstTime.value = "";
        $subwayLineLastTime.value = "";
        $subwayLineIntervalTime.value = "";
        $subwayLineId.value = "";
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
        $colorSelectContainer.insertAdjacentHTML("beforeend",
            colorSelectTemplate);
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
