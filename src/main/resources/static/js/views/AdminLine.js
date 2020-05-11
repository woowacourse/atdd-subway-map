import {EVENT_TYPE} from "../../utils/constants.js";
import api from "../../api/index.js"
import {colorSelectOptionTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";

function AdminLine() {
    const $subwayLineList = document.querySelector("#subway-line-list");
    const $subwayLineId = document.querySelector("#subway-line-id");
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

    function collectMessages(errorDtos) {
        const messages = [];
        for (let errorDto of errorDtos) {
            messages.push(errorDto.message);
        }
        return messages;
    }

    const onCreateSubwayLine = event => {
        event.preventDefault();
        if ($subwayLineId.value) {
            return;
        }
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
                subwayLineModal.toggle();
            })
            .catch(error => {
                if (error.status === 400) {
                    const messages = collectMessages(error.body);
                    alert(messages.join("\n"));
                    return;
                }
                alert(error.body.message)
            })
            .finally(() => {
                $subwayLineNameInput.value = "";
                $subwayLineColorInput.value = "";
            });
    };

    const onDeleteSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            event.preventDefault();
            const subwayLineId = $target.closest("div").id.split("subway-")[1];
            api.line.delete(subwayLineId)
                .then(response => {
                    $target.closest(".subway-line-item").remove();
                })
                .catch(alert);
        }
    };

    const onUpdateSubwayLine = event => {
        const $target = event.target;
        const isUpdateButton = $target.classList.contains("mdi-pencil");
        if (isUpdateButton) {
            subwayLineModal.toggle();
            const subwayLineId = $target.closest("div").id.split("subway-")[1];
            api.line.findById(subwayLineId)
                .then(response => {
                    const info = response.body;
                    $subwayFirstTime.value = info.startTime;
                    $subwayLastTime.value = info.endTime;
                    $subwayIntervalTime.value = info.intervalTime;
                    $subwayLineColorInput.value = info.bgColor;
                    $subwayLineNameInput.value = info.title;
                    $subwayLineId.value = info.id;
                });
        }
    };

    function renderInfo(info) {
        $subwayFirstTimeInfo.innerHTML = info.startTime;
        $subwayLastTimeInfo.innerHTML = info.endTime;
        $subwayIntervalTimeInfo.innerHTML = info.intervalTime + "분";
    }

    const onSelectSubwayLine = event => {
        const $target = event.target;
        const isSelectText = $target.classList.contains("subway-line-text");
        if (isSelectText) {
            event.preventDefault();
            const subwayLineId = $target.closest("div").id.split("subway-")[1];
            api.line.findById(subwayLineId)
                .then(response => renderInfo(response.body))
                .catch(alert)
        }
    };

    const onEditSubwayLine = event => {
        event.preventDefault();
        const subwayLineId = $subwayLineId.value;
        if (!subwayLineId) {
            return;
        }
        const editSubwayLine = {
            title: $subwayLineNameInput.value,
            startTime: $subwayFirstTime.value,
            endTime: $subwayLastTime.value,
            intervalTime: $subwayIntervalTime.value,
            bgColor: $subwayLineColorInput.value
        };

        api.line.update(subwayLineId, editSubwayLine)
            .then(response => {
                renderInfo(editSubwayLine);
                const bgSpan = document.querySelector("#subway-" + subwayLineId).children[0];
                bgSpan.classList.forEach(function (className) {
                    if (className.startsWith("bg")) {
                        bgSpan.classList.replace(className, editSubwayLine.bgColor);
                    }
                });
                subwayLineModal.toggle();
            })
            .catch(error => {
                if (error.status === 400) {
                    const messages = collectMessages(error.body);
                    alert(messages.join("\n"));
                    return;
                }
                alert(error.body.message)
            });
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
        $createSubwayLineButton.addEventListener(
            EVENT_TYPE.CLICK,
            onEditSubwayLine
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
