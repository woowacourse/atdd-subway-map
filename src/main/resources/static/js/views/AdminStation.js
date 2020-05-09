import {CONFIRM, ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE} from "../../utils/constants.js";
import {listItemTemplate} from "../../utils/templates.js";
import api from "../../api/index.js";

function AdminStation() {
    const $stationInput = document.querySelector("#station-name");
    const $stationList = document.querySelector("#station-list");
    const $stationAddBtn = document.querySelector("#station-add-btn");

    const initDefaultSubwayStations = () => {
        api.station.get()
            .then(response => {
                response.forEach(line => $stationList.insertAdjacentHTML(
                    "beforeend",
                    listItemTemplate
                    (line)
                ));
            });
    };

    function isValidate(stationName) {
        if (stationName === "") {
            alert(ERROR_MESSAGE.NOT_EMPTY);
            return false;
        }
        if (/\s/.test(stationName)) {
            alert(ERROR_MESSAGE.NOT_SPACE);
            return false;
        }
        if (/[0-9]+/.test(stationName)) {
            alert(ERROR_MESSAGE.NOT_NUMBER);
            return false;
        }
        const $listItem = document.querySelectorAll(".list-item");
        for (let i = 0; i < $listItem.length; i++) {
            if ($listItem.item(i).innerText === stationName) {
                alert(ERROR_MESSAGE.SAME_STATION_EXISTS);
                return false;
            }
        }
        return true;
    }

    function onAddStationHandler(event) {
        event.preventDefault();
        const $stationNameInput = document.querySelector("#station-name");
        const stationName = $stationNameInput.value;
        if (!isValidate(stationName)) {
            return;
        }
        const newStation = {
            name: stationName
        };
        api.station.create(newStation)
            .then(response => {
                $stationNameInput.value = "";
                $stationList.insertAdjacentHTML("beforeend", listItemTemplate(response));
            })
    }

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            let assureDelete = confirm(CONFIRM.DELETE_STATION);
            if (assureDelete) {
                const selectedItem = $target.closest(".list-item");
                const selectedItemId = selectedItem.getAttribute("data-id");
                api.station.delete(selectedItemId);
                selectedItem.remove();
            }
        }
    };
    const onKeyPressHandler = event => {
        if (event.key !== KEY_TYPE.ENTER) {
            return;
        }
        onAddStationHandler(event);
    };
    const initEventListeners = () => {
        $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onKeyPressHandler);
        $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
        $stationAddBtn.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
    };
    const init = () => {
        initEventListeners();
        initDefaultSubwayStations();
    };
    return {
        init
    };
}

const adminStation = new AdminStation();
adminStation.init();