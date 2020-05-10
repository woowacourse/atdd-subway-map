import {ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE} from "../../utils/constants.js";
import {listItemTemplate} from "../../utils/templates.js";
import api from "../../api/index.js";

function AdminStation() {
    const $stationInput = document.querySelector("#station-name");
    const $stationList = document.querySelector("#station-list");
    const $stationAddBtn = document.querySelector("#station-add-btn");

    const validateName = stationName => {
        if (stationName === "") {
            alert(ERROR_MESSAGE.NOT_EMPTY);
            return false;
        }
        if (stationName.includes(" ")) {
            alert(ERROR_MESSAGE.NOT_BLANK);
            return false;
        }
        if (/[\d]/g.test(stationName)) {
            alert(ERROR_MESSAGE.NOT_NUMBER);
            return false;
        }
        return true;
    }

    const onAddStationHandler = async event => {
        if (event.key !== KEY_TYPE.ENTER && event.type !== EVENT_TYPE.CLICK) {
            return;
        }
        event.preventDefault();
        const $stationNameInput = document.querySelector("#station-name");
        const stationName = $stationNameInput.value;
        if (!validateName(stationName)) {
            return;
        }

        const station = await api.station.create({"name": stationName})
        .then(data => data.json());
        $stationList.insertAdjacentHTML(
            "beforeend",
            listItemTemplate(station));
        $stationNameInput.value = "";
    };

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        const isYes = confirm("정말 삭제하시겠습니까?")
        if (isDeleteButton && isYes) {
            const $deleteStationItem = $target.closest(".list-item");
            const stationId = $deleteStationItem.dataset.stationId;
            api.station.delete(stationId)
            .then(() => $deleteStationItem.remove())
            .catch(error => console.log(error));
        }
    };

    const initSubwayStationsList = async () => {
        const stations = await api.station.get().then(data => data.json());
        stations.map(station =>
            $stationList.insertAdjacentHTML(
                "beforeend",
                listItemTemplate(station))
        );
    };

    const initEventListeners = () => {
        $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS,
            onAddStationHandler);
        $stationAddBtn.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
        $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    };

    const init = () => {
        initSubwayStationsList();
        initEventListeners();
    };

    return {
        init
    };
}

const adminStation = new AdminStation();
adminStation.init();