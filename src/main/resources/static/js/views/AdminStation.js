import {ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE} from "../../utils/constants.js";
import {listItemTemplate} from "../../utils/templates.js";

function AdminStation() {
    const $stationInput = document.querySelector("#station-name");
    const $stationAddBtn = document.querySelector("#station-add-btn");
    const $stationList = document.querySelector("#station-list");

    const onAddStationHandler = event => {
        if (event.key !== KEY_TYPE.ENTER) {
            return;
        }
        event.preventDefault();
        const $stationNameInput = document.querySelector("#station-name");
        const stationName = $stationNameInput.value;
        if (!stationName) {
            alert(ERROR_MESSAGE.NOT_EMPTY);
            return;
        }
        fetch("/stations", {
            method: 'post',
            headers: {
                'content-type': 'application/json'
            },
            body: JSON.stringify({
                name: stationName
            })
        }).then(res => {
            if (res.ok) {
                res.json().then(data => {
                    $stationNameInput.value = "";
                    $stationList.insertAdjacentHTML("beforeend", listItemTemplate(data));
                })
            }
        })
    };

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            $target.closest(".list-item").remove();
        }
    };

    const initEventListeners = () => {
        $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
        $stationAddBtn.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler)
        $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    };

    const init = () => {
        initEventListeners();
    };

    return {
        init
    };
}

const adminStation = new AdminStation();
adminStation.init();
