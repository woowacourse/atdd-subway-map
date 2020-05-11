import {ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE, MAGIC_NUMBER, NODE_NAME, REGEX} from "../../utils/constants.js";
import {listItemTemplate} from "../../utils/templates.js";
import api from "../../api/index.js";

function AdminStation() {
    const $stationInput = document.querySelector("#station-name");
    const $stationList = document.querySelector("#station-list");
    const $stationAddBtn = document.querySelector("#station-add-btn");

    const enterAddStation = event => {
        if (event.key !== KEY_TYPE.ENTER && event.type !== EVENT_TYPE.CLICK) {
            return;
        }
        onAddStationHandler(event);
    };

    const clickAddStationBtn = event => {
        onAddStationHandler(event);
    };

    const validateInput = (input) => {
        validateEmpty(input);
        validateNumber(input);
        validateSpace(input);
        validateSameStation(input);
    };

    const validateSameStation = input => {
        if (isSameStation(input)) {
            throw ERROR_MESSAGE.SAME_STATION_NAME;
        }
    }

    const validateNumber = input => {
        if (containsNumber(input)) {
            throw ERROR_MESSAGE.NUMBER;
        }
    }

    const validateSpace = input => {
        if (containsSpace(input)) {
            throw ERROR_MESSAGE.SPACE;
        }
    }

    const validateEmpty = input => {
        if (isEmpty(input)) {
            throw ERROR_MESSAGE.EMPTY;
        }
    }

    const containsNumber = input => {
        return REGEX.NUMBER.test(input);
    }

    const containsSpace = input => {
        return input.indexOf(REGEX.SPACE) > MAGIC_NUMBER.NOT_EXIST;
    }

    const isEmpty = input => {
        return input.length === 0;
    }

    const isSameStation = input => {
        let texts = convertToTexts($stationList.childNodes);
        return texts.includes(input);
    };

    const convertToTexts = nodes => {
        let texts = [];
        for (let node of nodes) {
            texts.push(node.textContent.trim());
        }
        return texts;
    }

    const onAddStationHandler = event => {
        event.preventDefault();
        const $stationNameInput = document.querySelector("#station-name");
        const stationName = $stationNameInput.value;
        // TODO : 이거 나중에 활성화
        // try {
        //   validateInput(stationName)
        // } catch (e) {
        //   alert(e);
        //   return;
        // }
        const stationRequest = {
            name: stationName
        };
        api.station.create(stationRequest).then(data => {
            if (data.error) {
                alert(data.error);
                return;
            }
            $stationList.insertAdjacentHTML("beforeend", listItemTemplate(data));
        });
        $stationNameInput.value = "";
    };

    const onRemoveStationHandler = event => {
        const $target = event.target;
        if ($target && $target.nodeName !== NODE_NAME.BUTTON && $target.nodeName !== NODE_NAME.SPAN) {
            return;
        }
        if (!confirm("정말로 삭제하시겠습니까?")) {
            return;
        }
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            api.station.delete($target.closest(".list-item").dataset.stationId);
            $target.closest(".list-item").remove();
        }
    };

    const initDefaultSubwayStation = () => {
        api.station.get().then(data => {
            data.map(station => {
                    $stationList.insertAdjacentHTML(
                        "beforeend",
                        listItemTemplate(station)
                    );
                }
            )
        });
    }

    const initEventListeners = () => {
        $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, enterAddStation);
        $stationAddBtn.addEventListener(EVENT_TYPE.CLICK, clickAddStationBtn);
        $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    };

    const init = () => {
        initDefaultSubwayStation();
        initEventListeners();
    };

    return {
        init
    };
}

const adminStation = new AdminStation();
adminStation.init();
