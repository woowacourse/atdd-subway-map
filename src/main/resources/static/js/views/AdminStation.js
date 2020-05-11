import {ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE} from "../../utils/constants.js";
import {listItemTemplate} from "../../utils/templates.js";
import api from "../../api/index.js";

function AdminStation() {
    const $stationInput = document.querySelector("#station-name");
    const $stationInputButton = document.querySelector("#station-add-btn");
    const $stationList = document.querySelector("#station-list");

    const onAddStationHandler = event => {
        if (event.key !== KEY_TYPE.ENTER && event.type !== EVENT_TYPE.CLICK) {
            return;
        }
        event.preventDefault();
        const $stationNameInput = document.querySelector("#station-name");
        const stationName = $stationNameInput.value;
        if (!stationName) {
            alert(ERROR_MESSAGE.NOT_EMPTY);
            return;
        }
        const data = {};
        data.name = stationName;
        api.station.create(data)
            .then(response => {
                $stationNameInput.value = "";
                $stationList.insertAdjacentHTML("beforeend", listItemTemplate(response.body, stationName));
            })
            .catch(alert);
    };

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            const span = $target.closest(".mdi-delete");
            const id = span.dataset.id;
            api.station.delete(id)
                .then(response => $target.closest(".list-item").remove())
                .catch(alert)
        }
    };

    const initEventListeners = () => {
        $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
        $stationInputButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
        $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    };

    const initSavedStations = () => {
        api.station.get()
            .then(response => {
                response.body.map(station => {
                    $stationList.insertAdjacentHTML(
                        "beforeend",
                        listItemTemplate(station.id, station.name)
                    );
                })

            })
            .catch(alert);
    };

    const init = () => {
        initSavedStations();
        initEventListeners();
    };

    return {
        init
    };
}

const adminStation = new AdminStation();
adminStation.init();
