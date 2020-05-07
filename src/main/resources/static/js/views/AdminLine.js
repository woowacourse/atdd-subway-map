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
    const $createSubwayLineButton = document.querySelector(
        "#subway-line-create-form #submit-button"
    );
    const subwayLineModal = new Modal();

    const form = {
        'subwayLineNameInput': document.querySelector("#subway-line-name"),
        'subwayLineColorInput': document.querySelector("#subway-line-color"),
        'subwayLineFirstTime': document.querySelector("#first-time"),
        'subwayLineLastTime': document.querySelector("#last-time"),
        'subwayLineIntervalTime': document.querySelector("#interval-time"),
        'subwayLineId': document.querySelector("#lineId")
    };
    const formManager = {
        close() {
            subwayLineModal.toggle();
            for (let key in form) {
                form[key].value = "";
            }
        },
        setting() {
            return {
                name: form.subwayLineNameInput.value,
                color: form.subwayLineColorInput.value,
                startTime: form.subwayLineFirstTime.value,
                endTime: form.subwayLineLastTime.value,
                intervalTime: form.subwayLineIntervalTime.value
            };
        }
    };

    const linesInfo = {
        'viewStartTime': document.querySelector("#view-start-time"),
        'viewEndTime': document.querySelector("#view-end-time"),
        'viewIntervalTime': document.querySelector("#view-interval-time")
    };
    const linesInfoManger = {
        clear() {
            linesInfo.viewStartTime.innerHTML = "";
            linesInfo.viewEndTime.innerHTML = "";
            linesInfo.viewIntervalTime.innerHTML = "";
        },
        setting(line) {
            linesInfo.viewStartTime.innerHTML = line.startTime.slice(0, 5);
            linesInfo.viewEndTime.innerHTML = line.endTime.slice(0, 5);
            linesInfo.viewIntervalTime.innerHTML = line.intervalTime + "분";
        }
    };

    function onCreateSubwayLine() {
        let newSubwayLine = formManager.setting();

        api.line.create(newSubwayLine).then(res => {
            if (res.status !== 201) {
                linesInfoManger.clear();
                return;
            }
            res.json().then(res => {
                newSubwayLine['id'] = res.id;
                addSubwayLineList(newSubwayLine);
                linesInfoManger.setting(newSubwayLine);
            });
        });
        formManager.close();
    };

    const onSelectSubwayLine = event => {
        const $target = event.target;
        const isSelectSubwayLine
            = $target.classList.contains("subway-line-item");
        if (isSelectSubwayLine) {
            api.line.get('/' + $target.id).then(line => {
                    linesInfoManger.setting(line);
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
                    form.subwayLineNameInput.value = res.name;
                    form.subwayLineColorInput.value = res.color;
                    form.subwayLineFirstTime.value = res.startTime;
                    form.subwayLineLastTime.value = res.endTime;
                    form.subwayLineIntervalTime.value = res.intervalTime;
                    form.subwayLineId.value = res.id;
                    subwayLineModal.toggle();
                }
            )
        }
    };

    function onUpdateSubwayLine() {
        let newSubwayLine = formManager.setting();

        api.line.update("/" + form.subwayLineId.value, newSubwayLine).then(
            res => {
                if (res.status !== 200) {
                    linesInfoManger.clear();
                    return;
                }
                res.json().then(res => {
                    newSubwayLine['id'] = res.id;
                    removeSubwayLineList(res.id);
                    addSubwayLineList(newSubwayLine);
                    linesInfoManger.setting(res);
                });
            });
        formManager.close();
    };

    const onDeleteSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (!isDeleteButton) {
            return;
        }

        const lineId = parseId($target.closest(".subway-line-item").id);
        api.line.delete("/" + lineId).then(response => {
            if (response.status !== 200) {
                alert("삭제불가!");
                throw new Error("HTTP status " + response.status);
            }
        }).then(() => $target.closest(".subway-line-item").remove());
    };

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
        $createSubwayLineButton.addEventListener(
            EVENT_TYPE.CLICK,
            save
        );
    };

    const save = event => {
        event.preventDefault();

        if (form.subwayLineId.value === "") {
            onCreateSubwayLine();
            return;
        }
        onUpdateSubwayLine();
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
