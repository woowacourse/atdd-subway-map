import {EVENT_TYPE} from "../../utils/constants.js";
import {colorSelectOptionTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminLine() {
    const UPDATE_BUTTON_CLASS_NAME = "mdi-pencil";
    const DELETE_BUTTON_CLASS_NAME = "mdi-delete";
    const $subwayLineList = document.querySelector("#subway-line-list");
    const $subwayLineNameInput = document.querySelector("#subway-line-name");
    const $subwayLineFirstTimeInput = document.querySelector("#first-time");
    const $subwayLineLastTimeInput = document.querySelector("#last-time");
    const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");
    const $createSubwayLineButton = document.querySelector("#subway-line-create-form #submit-button");
    const $subwayLineColorInput = document.querySelector("#subway-line-color");
    const subwayLineModal = new Modal();
    const $startTimeContainer = document.querySelector("#start-time-container");
    const $endTimeContainer = document.querySelector("#end-time-container");
    const $intervalTimeContainer = document.querySelector("#interval-time-container");

    let updatingId = 0;

    const showLineDetail = (data) => {
        $startTimeContainer.innerHTML = data.startTime;
        $endTimeContainer.innerHTML = data.endTime;
        $intervalTimeContainer.innerHTML = data.intervalTime;
    };

    const onSelectColorHandler = event => {
        event.preventDefault();
        const $target = event.target;
        if ($target.classList.contains("color-select-option")) {
            document.querySelector("#subway-line-color").value = $target.dataset.color;
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

    const onReadSubwayLine = event => {
        const lineId = event.currentTarget.firstElementChild.id;

        api.line
            .getBy(lineId)
            .then(data => showLineDetail(data));
    };

    const addClickListeners = () => {
        $subwayLineList.lastChild.addEventListener(
            EVENT_TYPE.CLICK,
            onReadSubwayLine
        );
        $subwayLineList.lastChild.querySelector(`.${DELETE_BUTTON_CLASS_NAME}`).addEventListener(
            EVENT_TYPE.CLICK,
            onDeleteSubwayLine
        );
        $subwayLineList.lastChild.querySelector(`.${UPDATE_BUTTON_CLASS_NAME}`).addEventListener(
            EVENT_TYPE.CLICK,
            onUpdateSubwayLine
        );
    };

    const addLineInView = (data) => {
        const newSubwayLine = {
            id: data.id,
            name: data.name,
            lineColor: data.lineColor
        };
        $subwayLineList.insertAdjacentHTML(
            "beforeend",
            subwayLinesTemplate(newSubwayLine)
        );
        addClickListeners();
        closeAndResetModalValue();
    };

    const saveLine = () => {
        return api.line.create({
            name: $subwayLineNameInput.value,
            startTime: $subwayLineFirstTimeInput.value,
            endTime: $subwayLineLastTimeInput.value,
            intervalTime: $subwayLineIntervalTimeInput.value,
            lineColor: $subwayLineColorInput.value
        });
    };

    const convertLineItemBy = (data) => {
        const lineId = data.id;
        const name = data.name;
        const lineColor = data.lineColor;

        let querySelector = document.getElementById(lineId).parentNode;

        querySelector.innerHTML =
            `<div class="id-class" id=${lineId}></div>
      <span class="${lineColor} w-3 h-3 rounded-full inline-block mr-1"></span>
      ${name}
      <button class="hover:bg-gray-300 hover:text-gray-500 text-gray-300 px-1 rounded-full float-right">
         <span class="mdi mdi-delete"></span>
      </button>
      <button class="hover:bg-gray-300 hover:text-gray-500 text-gray-300 px-1 rounded-full float-right">
         <span class="mdi mdi-pencil"></span>
      </button>`
    };

    const updateLine = () => {
        fetch(`/lines/${updatingId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                name: $subwayLineNameInput.value,
                startTime: $subwayLineFirstTimeInput.value,
                endTime: $subwayLineLastTimeInput.value,
                intervalTime: $subwayLineIntervalTimeInput.value,
                lineColor: $subwayLineColorInput.value
            })
        })
            .then(response => response.json())
            .then(data => {
                convertLineItemBy(data);
                closeAndResetModalValue()
            });
    };

    const saveOrUpdateLine = () => {
        if (updatingId) {
            updateLine();
            updatingId = 0;
            return;
        }
        let savedLine = saveLine();
        savedLine.then(data => {
            if (data.status === 201) {
                addLineInView(data);
            } else {
                errorHandle(data);
            }
        });
    };

    const errorHandle = response => {
        const errorFields = response.errors;

        if (!errorFields) {
            alert(response.response.message);
            return;
        }

        let $field, error;
        for (let i = 0, length = errorFields.length; i < length; i++) {
            error = errorFields[i];

            switch (error['field']) {
                case 'name' : {
                    $field = $('#subway-line-name');
                    break;
                }
                case 'intervalTime' : {
                    $field = $('#interval-time');
                    break;
                }
                case 'lineColor' : {
                    $field = $('#subway-line-color');
                    break;
                }
            }

            $field.attr('placeholder', error.defaultMessage);
        }
    }

    // @LineRequest의 Valid 어노테이션들을 시험하기 위해 무조건 유효한 값이라고 판단하도록 주석 처리
    const validInput = () => {
        // if ($subwayLineNameInput.value === "" ||
        //   $subwayLineFirstTimeInput.value === "" ||
        //   $subwayLineLastTimeInput.value === "" ||
        //   $subwayLineColorInput.value === "") {
        //   alert("값을 입력해야합니다.");
        //   return false;
        // }
        //
        //
        // if (!/\d/.test($subwayLineIntervalTimeInput.value)) {
        //   alert("숫자만 입력해주세요");
        //   return false;
        // }
        //
        // if ($subwayLineIntervalTimeInput.value <= 0) {
        //   alert("양수만 입력해주세요.");
        //   return false;
        // }
        return true;
    };

    const onCreateSubwayLine = (event) => {
        event.preventDefault();
        if (validInput()) {
            saveOrUpdateLine();
        }
    };

    const closeAndResetModalValue = () => {
        subwayLineModal.toggle();
        $subwayLineNameInput.value = "";
        $subwayLineFirstTimeInput.value = "";
        $subwayLineLastTimeInput.value = "";
        $subwayLineIntervalTimeInput.value = "";
        $subwayLineColorInput.value = "";
    };

    const onUpdateSubwayLine = event => {
        event.stopPropagation();
        const $target = event.target;
        const lineId = $target.closest(".subway-line-item").firstElementChild.id;

        const isUpdateButton = $target.classList.contains(UPDATE_BUTTON_CLASS_NAME);
        if (isUpdateButton) {
            api.line.getBy(lineId)
                .then(data => {
                    $subwayLineNameInput.value = data.name;
                    $subwayLineFirstTimeInput.value = data.startTime;
                    $subwayLineLastTimeInput.value = data.endTime;
                    $subwayLineIntervalTimeInput.value = data.intervalTime;
                    $subwayLineColorInput.value = data.lineColor;
                    updatingId = lineId;
                });
            closeAndResetModalValue();
        }
    };

    const onDeleteSubwayLine = event => {
        event.stopPropagation();
        const $target = event.target;
        const lineId = $target.closest(".subway-line-item").firstElementChild.id;

        const isDeleteButton = $target.classList.contains(DELETE_BUTTON_CLASS_NAME);
        if (isDeleteButton) {
            fetch(`/lines/${lineId}`, {
                method: "DELETE"
            });
            $target.closest(".subway-line-item").remove();
            $startTimeContainer.innerHTML = "00:00:00";
            $endTimeContainer.innerHTML = "00:00:00";
            $intervalTimeContainer.innerHTML = "00";
        }
    };

    const initEventListeners = () => {
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLine);
        $createSubwayLineButton.addEventListener(EVENT_TYPE.CLICK, onCreateSubwayLine);
    };

    const initDefaultSubwayLines = () => { // 이벤트 위임으로 처리하셨다.
        api.line.get()
            .then(data => {
                if (data.length === 0) {
                    return;
                }
                data.forEach(line =>
                    $subwayLineList.insertAdjacentHTML(
                        "beforeend",
                        subwayLinesTemplate(line)
                    )
                )
                ;
            });
    };

    this.init = () => {
        initDefaultSubwayLines();
        initEventListeners();
        initCreateSubwayLineForm();
    };
}

const adminLine = new AdminLine();
adminLine.init();
