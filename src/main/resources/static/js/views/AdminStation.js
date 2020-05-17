import {ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE} from "../../utils/constants.js";
import {listItemTemplate} from "../../utils/templates.js";
import api from "../../api/index.js";

function AdminStation() {
    const $stationInput = document.querySelector("#station-name");
    const $stationList = document.querySelector("#station-list");
    const $stationAddButton = document.querySelector("#station-add-btn");
    const onAddStationHandler = async event => {
        if (event.key !== KEY_TYPE.ENTER && event.type !== 'click') {
            return;
        }
        event.preventDefault();
        const $stationNameInput = document.querySelector("#station-name");
        const stationName = $stationNameInput.value;
        if (!stationName) {
            alert(ERROR_MESSAGE.NOT_EMPTY);
            return;
        }
        if (stationName.search(/\s/) !== -1) {
            alert(ERROR_MESSAGE.HAS_NOT_EMPTY);
            return;
        }
        if (stationName.search(/[0-9]/) !== -1) {
            alert(ERROR_MESSAGE.HAS_NOT_NUMBER);
            return;
        }
        const $listItems = document.querySelectorAll(".list-item");
        const $list = [];

        $listItems.forEach(value => $list.push(value.innerText));
        if ($list.includes(stationName)) {
            alert(ERROR_MESSAGE.NOT_DUPLICATE);
            return;
        }
        $stationNameInput.value = "";
        await api.station
            .create({'name': stationName}).then(response => {
                if (response.status !== 201) {
                    alert("역이 추가되지 않았습니다.");
                    return;
                }
                let location = response.headers.get('Location');
                api.station.getById(location).then(data =>
                    $stationList.insertAdjacentHTML("beforeend", listItemTemplate(data)))
            })
    };
    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (!(isDeleteButton && confirm("진짜 지울거에요?"))) {
            return;
        }
        let targetId = $target.closest(".list-item").dataset.stationId;
        api.station.delete(targetId)
            .then(() =>
                $target.closest(".list-item").remove()
            ).catch(err => alert(err));
    };
    const initEventListeners = () => {
        $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
        $stationAddButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
        $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    };
    const initStations = () => {
        api.station.getAll()
            .then(res => res.forEach(data =>
                $stationList.insertAdjacentHTML("beforeend", listItemTemplate(data))
            ))
            .catch(error => alert(error));
    }
    const init = () => {
        initEventListeners();
        initStations();
    };
    return {
        init
    };
}

const adminStation = new AdminStation();
adminStation.init();
