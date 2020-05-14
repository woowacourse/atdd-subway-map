import {ERROR_MESSAGE, EVENT_TYPE} from "../../utils/constants.js";
import {
    colorSelectOptionTemplate,
    subwayLineInfoTemplate,
    subwayLinesTemplate
} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminLine() {
    const $lineInfo = document.querySelector(".lines-info");
    const $lineList = document.querySelector("#subway-line-list");
    const $lineNameInput = document.querySelector("#subway-line-name");
    const $lineColorInput = document.querySelector("#subway-line-color");
    const $intervalTimeInput = document.querySelector("#interval-time");
    const $firstTimeInput = document.querySelector("#first-time");
    const $lastTimeInput = document.querySelector("#last-time");
    const $submitLineButton = document.querySelector(
        "#subway-line-create-form #submit-button"
    );
    const $cancelSubmitBtn = document.querySelector(".modal-close");
    const lineModal = new Modal();

    let $updateLineItem = null;

    const resetModalInputValue = () => {
        $lineNameInput.value = "";
        $firstTimeInput.value = "";
        $lastTimeInput.value = "";
        $intervalTimeInput.value = "";
        $lineColorInput.value = "";
    }

    const renderSubwayLineInfo = line => {
        $lineInfo.innerHTML = "";
        $lineInfo.insertAdjacentHTML(
            "beforeend",
            subwayLineInfoTemplate(line)
        );
    }

    const onShowLineInfoHandler = async event => {
        const $target = event.target.closest(".subway-line-item");
        event.preventDefault();

        const line = await api.line.getById($target.dataset.lineId)
        .then(data => data.json());
        renderSubwayLineInfo(line);
    }

    const getLineInputValues = () => {
        return {
            name: $lineNameInput.value,
            startTime: $firstTimeInput.value,
            endTime: $lastTimeInput.value,
            intervalTime: $intervalTimeInput.value,
            bgColor: $lineColorInput.value
        }
    }

    const validate = line => {
        for (let [key, value] of Object.entries(line)) {
            if (value === "") {
                alert(ERROR_MESSAGE.NOT_EMPTY);
                return false;
            }
            if (value.includes(" ")) {
                alert(ERROR_MESSAGE.NOT_BLANK);
                return false;
            }
        }
        return true;
    }

    const addLine = async () => {
        const newLine = getLineInputValues();
        if (!validate(newLine)) {
            return;
        }

        const line = await api.line.create(newLine)
        .then(data => data.json());
        $lineList.insertAdjacentHTML(
            "beforeend",
            subwayLinesTemplate(line)
        );

        lineModal.toggle();
        resetModalInputValue();
    };

    const onRemoveLineHandler = async event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (!isDeleteButton) {
            return;
        }

        const $deleteLineItem = $target.closest(".subway-line-item");
        await api.line.delete($deleteLineItem.dataset.lineId);
        $deleteLineItem.remove();
    };

    const onReadLineInfoToUpdateHandler = async event => {
        const $target = event.target;
        const isUpdateButton = $target.classList.contains("mdi-pencil");
        if (!isUpdateButton) {
            return;
        }

        lineModal.toggle();
        $submitLineButton.classList.add("update");

        $updateLineItem = $target.closest(".subway-line-item");
        const targetId = $updateLineItem.dataset.lineId;
        const line = await api.line.getById(targetId)
        .then(data => data.json());
        $lineNameInput.value = line.name;
        $firstTimeInput.value = line.startTime;
        $lastTimeInput.value = line.endTime;
        $intervalTimeInput.value = line.intervalTime;
        $lineColorInput.value = line.bgColor;
    };

    const updateLine = async () => {
        const updatedSubwayLine = getLineInputValues();

        const line = await api.line.update(
            $updateLineItem.dataset.lineId,
            updatedSubwayLine
        ).then(data => data.json());
        $updateLineItem.insertAdjacentHTML(
            "afterend",
            subwayLinesTemplate(line)
        );
        $updateLineItem.remove();
        renderSubwayLineInfo(line);

        lineModal.toggle();
        resetModalInputValue();
        $submitLineButton.classList.remove("update");
    };

    const onCancelSubmitHandler = event => {
        event.preventDefault();
        resetModalInputValue();

        if ($submitLineButton.classList.contains("update")) {
            $submitLineButton.classList.remove("update");
        }
    }

    const onSubmitLineHandler = event => {
        event.preventDefault();
        const $target = event.target;

        const isUpdate = $target.classList.contains("update");
        isUpdate ? updateLine() : addLine();
    }

    const initLines = async () => {
        const lines = await api.line.get().then(data => data.json());
        lines.map(line =>
            $lineList.insertAdjacentHTML(
                "beforeend",
                subwayLinesTemplate(line)
            )
        )
    };

    const initEventListeners = () => {
        $submitLineButton.addEventListener(
            EVENT_TYPE.CLICK,
            onSubmitLineHandler
        );
        $lineList.addEventListener(
            EVENT_TYPE.CLICK,
            onShowLineInfoHandler
        );
        $lineList.addEventListener(
            EVENT_TYPE.CLICK,
            onReadLineInfoToUpdateHandler
        );
        $lineList.addEventListener(
            EVENT_TYPE.CLICK,
            onRemoveLineHandler
        );
        $cancelSubmitBtn.addEventListener(
            EVENT_TYPE.CLICK,
            onCancelSubmitHandler
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

    const initCreateLineForm = () => {
        const $colorSelectContainer = document.querySelector(
            "#subway-line-color-select-container"
        );
        const colorSelectTemplate = subwayLineColorOptions
        .map((option, index) => colorSelectOptionTemplate(option, index))
        .join("");
        $colorSelectContainer.insertAdjacentHTML(
            "beforeend",
            colorSelectTemplate
        );
        $colorSelectContainer.addEventListener(
            EVENT_TYPE.CLICK,
            onSelectColorHandler
        );
    };

    this.init = () => {
        initLines();
        initEventListeners();
        initCreateLineForm();
    };
}

const adminLine = new AdminLine();
adminLine.init();
