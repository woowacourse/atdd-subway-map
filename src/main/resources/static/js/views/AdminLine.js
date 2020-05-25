import { EVENT_TYPE } from "../../utils/constants.js";
import api from "../../api/index.js";
import {
    subwayLinesTemplate,
    colorSelectOptionTemplate,
    lineInformationTemplate
} from "../../utils/templates.js";
import { subwayLineColorOptions } from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";

function AdminLine() {
    const $subwayLineList = document.querySelector("#subway-line-list");
    const $subwayLineNameInput = document.querySelector("#subway-line-name");
    const $subwayLineFirstTimeInput = document.querySelector("#first-time");
    const $subwayLineLastTImeInput = document.querySelector("#last-time");
    const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");
    const $subwayLineColorInput = document.querySelector("#subway-line-color");
    const $createSubwayLineButton = document.querySelector("#submit-button");
    let $activeSubwayLineItem = null;

    const subwayLineModal = new Modal();

    const onCreateSubwayLine = () => {
        let newSubwayLine = {
            title: $subwayLineNameInput.value,
            startTime: $subwayLineFirstTimeInput.value,
            endTime: $subwayLineLastTImeInput.value,
            intervalTime: $subwayLineIntervalTimeInput.value,
            bgColor: $subwayLineColorInput.value
        };

        api.line.create(newSubwayLine)
            .then(line => {
                if(!line.ok) {
                    throw (line.json());
                }
                $subwayLineList.insertAdjacentHTML(
                    "beforeend",
                    subwayLinesTemplate(line)
                );
                subwayLineModal.toggle();
            }).catch((line) => {
            line.then(data => {
                alert(data.message);
            })
        });
        // window.location.reload();
    };

    const onDeleteSubwayLine = event => {
        const $target = event.target;
        const $subwayLineItem = $target.closest(".subway-line-item");
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            api.line.delete($subwayLineItem.dataset.lineId)
                .then(() =>{
                    if(!line.ok) {
                        throw (line.json());
                    }
                $subwayLineItem.remove();
            })
                .catch((line) => {
                    line.then(data => {
                        alert(data.message);
                    })
                })
        }
    };

    const onEditSubwayLine = event => {
        const $target = event.target;
        const $subwayLineItem = $target.closest(".subway-line-item")
        $activeSubwayLineItem = $subwayLineItem
        const $submitButton =  document.querySelector('#submit-button')
        const isUpdateButton = $target.classList.contains("mdi-pencil");
        if (!isUpdateButton) {
            return
        }
        const lineId = $subwayLineItem.dataset.lineId;
        api.line.getById(lineId).then((line) => {
            if(!line.ok) {
                throw (line.json());
            }
            $subwayLineNameInput.value = line.title
            $subwayLineFirstTimeInput.value = line.startTime
            $subwayLineLastTImeInput.value = line.endTime
            $subwayLineIntervalTimeInput.value = line.intervalTime
            $subwayLineColorInput.value = line.bgColor
            subwayLineModal.toggle();
            $submitButton.classList.add('update-submit-button')
        }).catch((line) => {
            line.then
                .alert(line.message);
        })
    }

    const onUpdateSubwayLine = () => {
        const updatedSubwayLine = {
            title: $subwayLineNameInput.value,
            startTime: $subwayLineFirstTimeInput.value,
            endTime: $subwayLineLastTImeInput.value,
            intervalTime: $subwayLineIntervalTimeInput.value,
            bgColor: $subwayLineColorInput.value
        };
        api.line.update($activeSubwayLineItem.dataset.lineId, updatedSubwayLine)
            .then((line) =>{
                if(!line.ok) {
                    throw (line.json());
                }
            subwayLineModal.toggle();
            subwayLinesTemplate(line);
        }).catch((line) => {
            line.then
                .alert(line.message);
        })
    };

    const onSubmitHandler = (event) => {
        event.preventDefault()
        const $target = event.target;
        const isUpdateSubmit = $target.classList.contains("update-submit-button");
        isUpdateSubmit ? onUpdateSubwayLine($target) : onCreateSubwayLine();
    }

    const onSelectSubwayLine = event => {
        const $target = event.target;
        const $subwayLineItem = $target.closest(".subway-line-item");
        const isSelectSubwayLineItem = $target.classList.contains("subway-line-item");
        if (isSelectSubwayLineItem) {
            api.line.getById($subwayLineItem.dataset.lineId).then(data => {
                if(!data.ok) {
                    throw (data.json());
                }
                document.querySelector(".lines-info").innerHTML = lineInformationTemplate(data);
            }).catch(data => {
                data.then
                    .alert(data.message);
            });
        }
    }

    const initDefaultSubwayLines = () => {
        api.line.get().then(lines => {
            if(!lines.ok) {
                throw (lines.json())
            }
            lines.map(line => {
                $subwayLineList.insertAdjacentHTML(
                    "beforeend",
                    subwayLinesTemplate(line)
                );
            });
        }).catch(lines => {
            lines.then
                .alert(lines.message);
        })
    };

    const initEventListeners = () => {
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onSelectSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onEditSubwayLine);
        $createSubwayLineButton.addEventListener(
            EVENT_TYPE.CLICK,
            onSubmitHandler
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
