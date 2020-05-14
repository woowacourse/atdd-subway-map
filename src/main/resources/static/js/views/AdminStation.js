import {ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE} from "../../utils/constants.js";
import {listItemTemplate} from "../../utils/templates.js";

function AdminStation() {
    const $stationInput = document.querySelector("#station-name");
    const $stationAddBtn = document.querySelector("#station-add-btn");
    const $stationList = document.querySelector("#station-list");

    const initStationsList = () => {
        fetch("/stations", {
            method: 'get'
        }).then(res => res.json())
            .then(data => {
                data.map(station =>
                    $stationList.insertAdjacentHTML("beforeend", listItemTemplate(station)));
            })
    };

    const onAddStationHandlerPressEnter = event => {
        if (event.key !== KEY_TYPE.ENTER) {
            return;
        }
        event.preventDefault();
        addStation();
    };

    function addStation() {
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
            if (!res.ok) {
                throw res
            }
            res.json().then(data => {
                $stationNameInput.value = "";
                $stationList.insertAdjacentHTML("beforeend", listItemTemplate(data));
            })
        }).catch(error => {
            error.text().then(msg => alert(msg));
        })
    }

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const $id = $target.closest(".list-item").dataset.stationId;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            if (isDeleteButton && confirm("지우시겠습니까?")) {
                fetch("/stations/" + $id, {
                    method: 'delete'
                }).then(res => {
                    if (res.ok) {
                        $target.closest(".list-item").remove();
                    }
                });
            }
        }
    };

    const initEventListeners = () => {
        $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandlerPressEnter);
        $stationAddBtn.addEventListener(EVENT_TYPE.CLICK, addStation)
        $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    };

    const init = () => {
        initStationsList();
        initEventListeners();
    };

    return {
        init
    };
}

const adminStation = new AdminStation();
adminStation.init();
