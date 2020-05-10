import {EVENT_TYPE, KEY_TYPE, STATION_INPUT_ERROR_MESSAGE, TRANSFER_ERROR_MESSAGE} from "../../utils/constants.js";
import {listItemTemplate} from "../../utils/templates.js";
import api from "../../api/index.js";

function AdminStation() {
    const $stationInput = document.querySelector("#station-name");
    const $stationAddBtn = document.querySelector("#station-add-btn");
    const $stationList = document.querySelector("#station-list");

    const onAddStationHandler = event => {
        if (event.type !== EVENT_TYPE.CLICK && event.key !== KEY_TYPE.ENTER) {
            return;
        }
        event.preventDefault();
        const stationName = $stationInput.value;

        const errorMsg = getInvalidNameErrorMsg(stationName);
        if (errorMsg) {
            alert(errorMsg);
            return;
        }

        const stationData = {
            name: stationName
        };
        api.station.create(stationData)
            .then(station => {
                $stationList.insertAdjacentHTML("beforeend", listItemTemplate(station));
            }).catch(() => {
            alert(TRANSFER_ERROR_MESSAGE.WARN);
        });

        $stationInput.value = "";
    };

    const getInvalidNameErrorMsg = (stationName) => {
        let errorMsg;
        if (!stationName) {
            errorMsg = STATION_INPUT_ERROR_MESSAGE.NOT_EMPTY;
        }
        if (Array.from(stationName).some(c => c === " ")) {
            errorMsg = STATION_INPUT_ERROR_MESSAGE.BLANK_EXIST;
        }
        if (Array.from(stationName).some(c => /[0-9]/g.test(c))) {
            errorMsg = STATION_INPUT_ERROR_MESSAGE.NUMBER_EXIST;
        }
        const $stationList = document.querySelector("#station-list");
        const names = $stationList.getElementsByTagName("div");
        for (let i = 0; i < names.length; i++) {
            if (names[i].innerText === stationName) {
                errorMsg = STATION_INPUT_ERROR_MESSAGE.DUPLICATION;
            }
        }
        return errorMsg;
    }

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            const $id = $target.dataset.stationId;
            api.station.delete($id).then();
            $target.closest(".list-item").remove();
        }
    };

    const initEventListeners = () => {
        $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
        $stationAddBtn.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
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
