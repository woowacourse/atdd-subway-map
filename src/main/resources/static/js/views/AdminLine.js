import {ERROR_MESSAGE, EVENT_TYPE} from "../../utils/constants.js";
import {colorSelectOptionTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";

function AdminLine() {
    const $subwayLineList = document.querySelector("#subway-line-list");
    const $subwayLineNameInput = document.querySelector("#subway-line-name");
    const $subwayStartTimeInput = document.querySelector("#first-time");
    const $subwayLastTimeInput = document.querySelector("#last-time");
    const $intervalTimeInput = document.querySelector("#interval-time");
    const $subwayLineColorInput = document.querySelector("#subway-line-color");

    const $createSubwayLineButton = document.querySelector(
        "#subway-line-create-form #submit-button"
    );
    const subwayLineModal = new Modal();

    const onCreateSubwayLine = event => {
        event.preventDefault();
        if (!$subwayLineNameInput.value) {
            return alert(ERROR_MESSAGE.NOT_EMPTY);
        }

        const newSubwayLine = {
            title: $subwayLineNameInput.value,
            startTime: $subwayStartTimeInput.value,
            endTime: $subwayLastTimeInput.value,
            intervalTime: $intervalTimeInput.value,
            bgColor: $subwayLineColorInput.value
        };

        let url = "/lines";
        let createRequest = {
            headers: {
                'Content-Type': 'application/json'
            },
            method: 'POST',
            body: JSON.stringify({
                title: newSubwayLine.title,
                startTime: newSubwayLine.startTime,
                endTime: newSubwayLine.endTime,
                intervalTime: newSubwayLine.intervalTime,
                bgColor: newSubwayLine.bgColor
            })
        };

        fetch(url, createRequest)
            .then(res => {
                if (res.ok) {
                    $subwayLineList.insertAdjacentHTML(
                        "beforeend",
                        subwayLinesTemplate(newSubwayLine)
                    )
                } else {
                    alert("중복된 이름입니다");
                }
            });

        subwayLineModal.toggle();
        $subwayLineNameInput.value = "";
        $subwayLineColorInput.value = "";
        $subwayStartTimeInput.value = "";
        $subwayLastTimeInput.value = "";
        $intervalTimeInput.value = "";
    };

    const onDeleteSubwayLine = event => {
        const $target = event.target;
        const $title = $target.closest(".subway-line-item").innerText.trim();
        console.log($name);
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton && confirm("지우시겠습니까?")) {
            let deleteRequest = {
                headers: {
                    'Content-Type': 'application/json'
                },
                method: 'delete',
                body: JSON.stringify({
                    title: $title
                })
            };

            fetch("/lines", deleteRequest).then(res => {
                if (res.ok) {
                    $target.closest(".subway-line-item").remove();
                }
            });
        }
    };


    const onUpdateSubwayLine = event => {
        const $target = event.target;
        const isUpdateButton = $target.classList.contains("mdi-pencil");
        if (isUpdateButton) {
            subwayLineModal.toggle();
        }
    };

    const onEditSubwayLine = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-pencil");
    };

    const onSelectColorHandler = event => {
        event.preventDefault();
        const $target = event.target;
        if ($target.classList.contains("color-select-option")) {
            document.querySelector("#subway-line-color").value =
                $target.dataset.color;
        }
    };

    this.init = () => {
        initDefaultSubwayLines();
        initEventListeners();
        initCreateSubwayLineForm();
    };

    const initDefaultSubwayLines = () => {
        fetch("/lines", {
            headers: {
                'Content-Type': 'application/json'
            },
            method: 'get'
        }).then(res => res.json())
            .then(data => data.map(
                line => {
                    $subwayLineList.insertAdjacentHTML(
                        "beforeend",
                        subwayLinesTemplate(line)
                    )
                }))
    };

    const initEventListeners = () => {
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLine);
        $createSubwayLineButton.addEventListener(
            EVENT_TYPE.CLICK,
            onCreateSubwayLine
        );
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
}

const adminLine = new AdminLine();
adminLine.init();
