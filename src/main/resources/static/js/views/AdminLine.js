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
    const $createSubwayLineButton =
        document.querySelector("#subway-line-create-form #submit-button");
    const subwayLineModal = new Modal();
    const linesInfo = {
        'viewStartTime': document.querySelector("#view-start-time"),
        'viewEndTime': document.querySelector("#view-end-time"),
        'viewIntervalTime': document.querySelector("#view-interval-time"),
        clear() {
            for (let key in this) {
                if (typeof this.key === "object") {
                    this.key.innerHTML = "";
                }
            }
        },
        setBy(line) {
            this.viewStartTime.innerHTML = line.startTime.slice(0, 5);
            this.viewEndTime.innerHTML = line.endTime.slice(0, 5);
            this.viewIntervalTime.innerHTML = line.intervalTime + "분";
        }
    };

    function settingLineList(statusCode, res) {
        if (res.status !== statusCode) {
            linesInfo.clear();
            return;
        }
        res.json().then(res => {
            removeSubwayLineList(res.id);
            addSubwayLineList(res);
            linesInfo.setBy(res);
        });
    }

    const onSelectSubwayLine = event => {
        const $target = event.target;
        const isSelectSubwayLine
            = $target.classList.contains("subway-line-item");
        if (isSelectSubwayLine) {
            api.line.get('/' + parseId($target.id)).then(line => {
                    linesInfo.setBy(line);
                }
            )
        }
    };

    function parseId(target) {
        return target.split("-")[1];
    }

    const onEditSubwayLine = event => {
        const $target = event.target;
        const isUpdateButton = $target.classList.contains("mdi-pencil");
        if (isUpdateButton) {
            const lineId = parseId($target.closest(".subway-line-item").id);
            api.line.get('/' + lineId).then(res => {
                    subwayLineModal.toggle();
                    subwayLineModal.setBy(res);
                }
            )
        }
    };

    const onDeleteSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (!isDeleteButton) {
            return;
        }

        const lineId = parseId($target.closest(".subway-line-item").id);
        api.line.delete("/" + lineId).then(res => {
            if (res.status !== 200) {
                alert("삭제불가!");
                return;
            }
            $target.closest(".subway-line-item").remove()
        });
    };

    function addSubwayLineList(newSubwayLine) {
        $subwayLineList.insertAdjacentHTML(
            "beforeend",
            subwayLinesTemplate(newSubwayLine)
        );
    }

    function removeSubwayLineList(id) {
        let selectId = "#line-" + id;
        let removeSubwayLine = document.querySelector(selectId);
        if (removeSubwayLine) {
            $subwayLineList.removeChild(document.querySelector(selectId));
        }
    }

    const initDefaultSubwayLines = () => {
        api.line.get().then(newSubwayLines => {
            newSubwayLines.forEach(newSubwayLine => {
                addSubwayLineList(newSubwayLine)
            })
        });
    };

    const initEventListeners = () => {
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onEditSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onSelectSubwayLine);
        $createSubwayLineButton.addEventListener(EVENT_TYPE.CLICK, save);
    };

    const save = event => {
        event.preventDefault();
        if (subwayLineModal.subwayLineId() === "") {
            onCreateSubwayLine();
            return;
        }
        onUpdateSubwayLine();
    };

    const onCreateSubwayLine = () => {
        let newSubwayLine = subwayLineModal.makeFrom();
        api.line.create(newSubwayLine).then(
            res => settingLineList(201, res));
        subwayLineModal.toggle();
    };

    const onUpdateSubwayLine = () => {
        let newSubwayLine = subwayLineModal.makeFrom();
        api.line.update("/" + subwayLineModal.subwayLineId(),
            newSubwayLine).then(
            res => settingLineList(200, res));
        subwayLineModal.toggle();
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
