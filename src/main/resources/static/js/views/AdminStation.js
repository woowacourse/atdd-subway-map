import {ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE} from "../../utils/constants.js";
import {listItemTemplate} from "../../utils/templates.js";
import api from "../../api/index.js";


function AdminStation() {
    let stations = [];
    const $stationInput = document.querySelector('#station-name');
    const $stationList = document.querySelector('#station-list');
    const $stationAddButton = document.querySelector('#station-add-btn');

    const onAddStationHandler = (event) => {
        if (event.key !== KEY_TYPE.ENTER && event.key !== KEY_TYPE.CLICK) {
            return;
        }
        event.preventDefault();
        const $stationNameInput = document.querySelector('#station-name');
        const stationName = $stationNameInput.value;
        const blank_pattern = /^[^\d\s]+$/;
        if (!stationName) {
            alert(ERROR_MESSAGE.NOT_EMPTY);
            return;
        }
        if (!blank_pattern.test(stationName)) {
            alert(ERROR_MESSAGE.NOT_BLANK);
            return;
        }
        if (stations.includes(stationName)) {
            alert(ERROR_MESSAGE.NOT_DUPLICATE);
            return;
        }

        stations = [...stations, stationName];

        $stationNameInput.value = '';
        $stationList.insertAdjacentHTML('beforeend', listItemTemplate(stationName));
        api.station.create(stationName);
    };

    const onRemoveStationHandler = (event) => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains('mdi-delete');
        const stationName = $target.closest('.list-item').innerText;
        if (isDeleteButton && confirm('정말 삭제하시겠습니까?')) {
            $target.closest('.list-item').remove();
            stations = stations.filter((station) => station !== stationName);
        }
    };

    const initEventListeners = () => {
        $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
        $stationAddButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
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
