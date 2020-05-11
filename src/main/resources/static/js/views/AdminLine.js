import {EVENT_TYPE} from "../../utils/constants.js";
import {colorSelectOptionTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import api from "../../api/index.js";
import Modal from "../../ui/Modal.js";

function AdminLine() {
    const $subwayLineList = document.querySelector("#subway-line-list");
    const $createSubwayLineButton =
        document.querySelector("#subway-line-create-form #submit-button");
    const subwayLineModal = new Modal();
    const changeInfo = subwayLineModal.$changeInfo;
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

    const settingLineList = (statusCode, res, newSubwayLine) => {
        if (res.status !== statusCode) {
            res.json().then(data => {
                let errors = "";
                console.log(data["errors"]);
                data["errors"].forEach(
                    err => errors += err['defaultMessage']
                );
                return alert(errors)
            });
            linesInfo.clear();
            return;
        }

        if (changeInfo.target !== null) {
            console.log(newSubwayLine);
            updateSubwayLineList(newSubwayLine);
        } else {
            let strings = res.headers.get("Location").split("/");
            const id = strings[strings.length - 1];
            addSubwayLineList(newSubwayLine, id);
        }

        linesInfo.setBy(res);
    };

    const addSubwayLineList = (newSubwayLine, id) => {
        newSubwayLine['id'] = id;
        $subwayLineList.insertAdjacentHTML(
            "beforeend",
            subwayLinesTemplate(newSubwayLine)
        );
    };

    const updateSubwayLineList = newSubwayLine => {
        changeInfo.target.querySelector("span").classList.remove(
            changeInfo.beforeColor);
        changeInfo.target.querySelector("span").classList.add(newSubwayLine.color);
        changeInfo.target.innerHTML = changeInfo.target.innerHTML.replace(
            changeInfo.beforeName, newSubwayLine.name);
    };

    const onSelectSubwayLine = event => {
        const $target = event.target;
        const isSelectSubwayLine
            = $target.classList.contains("subway-line-item");
        if (isSelectSubwayLine) {
            api.line.get('/' + $target.dataset.lineId).then(line => {
                    linesInfo.setBy(line);
                }
            )
        }
    };

    const onEditSubwayLine = event => {
        const $target = event.target;
        const isUpdateButton = $target.classList.contains("mdi-pencil");
        if (isUpdateButton) {
            changeInfo.target = $target.closest(".subway-line-item");
            const lineId = changeInfo.target.dataset.lineId;
            api.line.get('/' + lineId).then(res => {
                    changeInfo.beforeName = res.name;
                    changeInfo.beforeColor = res.color;
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

        const lineId = $target.closest(".subway-line-item").dataset.lineId;
        api.line.delete("/" + lineId).then(res => {
            if (res.status !== 200) {
                console.log(res.body);
                return;
            }
            $target.closest(".subway-line-item").remove()
        });
    };

    const initDefaultSubwayLines = () => {
        api.line.get().then(newSubwayLines => {
            newSubwayLines.forEach(newSubwayLine => {
                addSubwayLineList(newSubwayLine, newSubwayLine['id'])
            })
        });
    };

    const initEventListeners = () => {
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onEditSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onSelectSubwayLine);
        $createSubwayLineButton.addEventListener(EVENT_TYPE.CLICK, save);
    };

    const save = (event) => {
        event.preventDefault();
        if (subwayLineModal.subwayLineId() === "") {
            onCreateSubwayLine();
            return;
        }
        onUpdateSubwayLine();
    };

    const onCreateSubwayLine = () => {
        let newSubwayLine = subwayLineModal.makeFrom();
        console.log(newSubwayLine);
        api.line.create(newSubwayLine).then(
            res => settingLineList(201, res, newSubwayLine))
        // .catch(err => alert(err))
        subwayLineModal.toggle();
    };

    const onUpdateSubwayLine = () => {
        let newSubwayLine = subwayLineModal.makeFrom();
        api.line.update("/" + subwayLineModal.subwayLineId(),
            newSubwayLine).then(
            res => settingLineList(200, res, newSubwayLine))
        // .catch(err => alert(err));
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
