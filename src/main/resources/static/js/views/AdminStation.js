import {ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE} from "../../utils/constants.js";
import {listItemTemplate} from "../../utils/templates.js";

function AdminStation() {
    const $stationInput = document.querySelector("#station-name");
    const $stationList = document.querySelector("#station-list");
    const $stationAddButton = document.querySelector("#station-add-btn");

    const onAddStationHandler = event => {
        console.log(event);
        if (event.key !== KEY_TYPE.ENTER && event.type !== EVENT_TYPE.CLICK) {
            return;
        }
        event.preventDefault();
        const $stationNameInput = document.querySelector("#station-name");
        const $stationName = $stationNameInput.value;
        console.log($stationNameInput.value);
        if (NotValidationOf($stationName)) {
            return;
        }
        fetch("/stations", {
            method: "POST",
            headers: {
                'content-type': 'application/json'
            },
            body: JSON.stringify({
                name: $stationName
            })
        }).then(res => {
            if (res.ok) {
                res.json().then(data => {
                    $stationNameInput.value = "";
                    $stationList.insertAdjacentHTML("beforeend", listItemTemplate(data));
                })
            } else {
                alert("지하철역을 불러오지 못했습니다.");
            }
        });
    };

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        const $id = $target.closest(".list-item").dataset.lineId;
        if (isDeleteButton) {
            if (confirm("정말로 삭제하시겠습니까?")) {
                fetch("/stations/" + $id, {
                    method: "DELETE",
                    body: {
                        id: $id
                    }
                }).then(res => {
                    if (res.ok) {
                        $target.closest(".list-item").remove();
                    } else {
                        alert("지하철역을 지우는데 에러가 발생하였습니다.");
                    }
                });
            }
        }
    };

    const initEventListeners = () => {
        $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
        $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
        $stationAddButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
    };

    const init = () => {
        initDefaultSubwayLines();
        initEventListeners();
    };

    const initDefaultSubwayLines = () => {
        fetch("/stations", {
            headers: {
                'Content-Type': 'application/json'
            },
            method: 'GET'
        }).then(res => res.json())
            .then(data => data.map(
                station => {
                    $stationList.insertAdjacentHTML(
                        "beforeend",
                        listItemTemplate(station)
                    )
                }))
    };

    const NotValidationOf = stationName => {
        if (!stationName) {
            alert(ERROR_MESSAGE.NOT_EMPTY);
            return true;
        }
        if (stationName.match(/[0-9]+/)) {
            alert(ERROR_MESSAGE.NOT_NUMBER);
            return true;
        }
        if (stationName.match(/\s/)) {
            alert(ERROR_MESSAGE.NOT_EMPTY_SPACE);
            return true;
        }
        const $lists = document.querySelectorAll(".list-item");
        for (let i = 0; i < $lists.length; i++) {
            if ($lists[i].dataset.stationName === stationName) {
                alert(ERROR_MESSAGE.NOT_SAME_STATATION);
                return true;
            }
        }
        return false;
    };

    return {
        init
    };
}

const adminStation = new AdminStation();
adminStation.init();
