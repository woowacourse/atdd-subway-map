import {CONFIRM_MESSAGE, ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE} from "../../utils/constants.js";
import {listItemTemplate} from "../../utils/templates.js";
import api from "../../api/index.js";

function AdminStation() {
    const $stationInput = document.querySelector("#station-name");
    const $stationList = document.querySelector("#station-list");
    const $stationAddButton = document.querySelector("#station-add-btn");

    const onAddStationHandler = event => {
        if (event.type !== EVENT_TYPE.CLICK && event.key !== KEY_TYPE.ENTER) {
            return;
        }

        event.preventDefault();
        const $stationNameInput = document.querySelector("#station-name");
        const stationName = $stationNameInput.value;
        const regExp = new RegExp(/^[^\d\s]+$/);

        if (!stationName) {
            alert(ERROR_MESSAGE.NOT_EMPTY);
            return;
        }

        if (validateReduplication(stationName)) {
            $stationNameInput.value = "";
            alert(ERROR_MESSAGE.REDUPLCATE_STATION_NAME);
            return;
        }

        if (!regExp.test(stationName)) {
            alert(ERROR_MESSAGE.INCORRECT_STATION_NAME);
            $stationNameInput.value = "";
            return;
        }

        const station = {
            name: stationName
        };

        $stationNameInput.value = "";
        api.station.create(station).then(data => {
            $stationList.insertAdjacentHTML("beforeend", listItemTemplate(data));
        })
    };

    function validateReduplication(stationName) {
        const items = document.querySelectorAll('.list-item');
        let isReduplicate = false;

        items.forEach(item => {
            if (item.innerText === stationName) {
                isReduplicate = true;
                return;
            }
        });

        return isReduplicate;
    }

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        const stationId = $target.closest(".list-item").dataset.id;

        if (isDeleteButton) {
            const isRemove = confirm(CONFIRM_MESSAGE.REMOVE);

            if (!isRemove) {
                return;
            }

            api.station.delete(stationId).then(() =>
                $target.closest(".list-item").remove());
        }
    };

    const initEventListeners = () => {
        $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
        $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
        $stationAddButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
    };

    const initStations = () => {
        api.station.get().then(data => {
            data.map(line => {
                $stationList.insertAdjacentHTML("beforeend", listItemTemplate(line));
            })
        })
    };

    const init = () => {
        initStations();
        initEventListeners();
    };

    return {
        init
    };
}

const adminStation = new AdminStation();
adminStation.init();