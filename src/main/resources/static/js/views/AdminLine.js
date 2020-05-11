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
    let $isEdit = false;
    let $editId = "";

    const $createSubwayLineButton = document.querySelector(
        "#subway-line-create-form #submit-button"
    );
    const $cancelSubWayLineButton = document.querySelector(
        "#subway-line-cancel-button"
    );

    const subwayLineModal = new Modal();

    const onCreateSubwayLine = event => {
        event.preventDefault();
        if (!$subwayLineNameInput.value) {
            return alert(ERROR_MESSAGE.NOT_EMPTY);
        }

        let url = "/lines";
        let createRequest = {
            headers: {
                'Content-Type': 'application/json'
            },
            method: 'POST',
            body: JSON.stringify({
                title: $subwayLineNameInput.value,
                startTime: $subwayStartTimeInput.value,
                endTime: $subwayLastTimeInput.value,
                intervalTime: $intervalTimeInput.value,
                bgColor: $subwayLineColorInput.value
            })
        };

        fetch(url, createRequest)
            .then(res => {
                    if (res.ok) {
                        res.json().then(data => {
                            $subwayLineList.insertAdjacentHTML(
                                "beforeend",
                                subwayLinesTemplate(data)
                            )
                        })
                    } else {
                        console.log(res);
                        console.log(res.body);
                    }
                }
            );

        subwayLineModal.toggle();
    };

    const onDeleteSubwayLine = event => {
        const $target = event.target;
        const $id = $target.closest(".subway-line-item").dataset.lineId;
        console.log($id);
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton && confirm("지우시겠습니까?")) {
            fetch("/lines/" + $id, {
                method: 'delete'
            }).then(res => {
                if (res.ok) {
                    $target.closest(".subway-line-item").remove();
                }
            });
        }
    };


    const onUpdateSubwayLine = event => {
        $isEdit = true;
        const $target = event.target;
        const isUpdateButton = $target.classList.contains("mdi-pencil");
        const $id = $target.closest(".subway-line-item").dataset.lineId;
        $editId = $id;
        if (isUpdateButton) {
            fetch(/lines/ + $id, {
                method: 'get'
            }).then(res => {
                    if (res.ok) {
                        res.json().then(data => {
                            $subwayLineNameInput.value = data.title;
                            $subwayLineColorInput.value = data.bgColor;
                            $subwayStartTimeInput.value = data.startTime;
                            $subwayLastTimeInput.value = data.endTime;
                            $intervalTimeInput.value = data.intervalTime;
                        })
                    }
                }
            );
            subwayLineModal.toggle();
        }
    };

    const onEditSubwayLine = () => {
        let url = "/lines/" + $editId;
        let editRequest = {
            headers: {
                'Content-Type': 'application/json'
            },
            method: 'put',
            body: JSON.stringify({
                title: $subwayLineNameInput.value,
                startTime: $subwayStartTimeInput.value,
                endTime: $subwayLastTimeInput.value,
                intervalTime: $intervalTimeInput.value,
                bgColor: $subwayLineColorInput.value
            })
        };
        fetch(url, editRequest)
            .then(res => {
                    if (!res.ok) {
                        alert("중복된 이름입니다");
                    }
                }
            );
        subwayLineModal.toggle();
    };

    const onInfoSubwayLine = event => {
        const $target = event.target;
        const $id = $target.closest(".subway-line-item").dataset.lineId;
        const isUpdateButton = $target.classList.contains("mdi-pencil");
        const isDeleteButton = $target.classList.contains("mdi-delete");

        if (!(isUpdateButton || isDeleteButton)) {
            fetch("/lines/" + $id, {
                method: 'GET',
                headers: {
                    'content-type': 'application/json'
                }
            }).then(res => {
                if (res.ok) {
                    res.json().then(data => {
                        document.querySelector("#info-first-time").innerHTML = data.startTime;
                        document.querySelector("#info-last-time").innerHTML = data.endTime;
                        document.querySelector("#info-interval-time").innerHTML = data.intervalTime;
                    })
                } else {
                    alert("데이터를 불러오지 못했습니다.");
                }
            })
        }

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
        $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onInfoSubwayLine);

        $createSubwayLineButton.addEventListener(
            EVENT_TYPE.CLICK, function (event) {
                if ($isEdit) {
                    onEditSubwayLine(event);
                } else {
                    onCreateSubwayLine(event);
                }
                stateInit();
            }
        );

        $cancelSubWayLineButton.addEventListener(
            EVENT_TYPE.CLICK, function () {
                stateInit();
            }
        )
    };

    function stateInit() {
        $isEdit = false;
        $editId = "";
        $subwayLineNameInput.value = "";
        $subwayLineColorInput.value = "";
        $subwayStartTimeInput.value = "";
        $subwayLastTimeInput.value = "";
        $intervalTimeInput.value = "";
    }

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
