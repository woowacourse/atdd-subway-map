import {EVENT_TYPE} from "../utils/constants.js";

export default function Modal() {
    const $openModalButton = document.querySelector(".modal-open");
    const $closeModalButton = document.querySelector(".modal-close");
    const $body = document.querySelector("body");
    const $modal = document.querySelector(".modal");
    const $forms = {
        'subwayLineNameInput': document.querySelector("#subway-line-name"),
        'subwayLineColorInput': document.querySelector("#subway-line-color"),
        'subwayLineFirstTime': document.querySelector("#first-time"),
        'subwayLineLastTime': document.querySelector("#last-time"),
        'subwayLineIntervalTime': document.querySelector("#interval-time"),
        'subwayLineId': document.querySelector("#lineId"),
    };
    const $changeInfo = {
        target: null,
        beforeName: null,
        beforeColor: null,
    };

    const onOpenModalButtonClick = event => {
        if (event && event.target.id === 'subway-line-add-btn') {
            for (let key in $changeInfo) {
                $changeInfo[key] = null;
            }
        }
    };

    const toggle = event => {
        if (event) {
            event.preventDefault();
        }
        $body.classList.toggle("modal-active");
        $modal.classList.toggle("opacity-0");
        $modal.classList.toggle("pointer-events-none");
        onOpenModalButtonClick(event);
        for (let key in $forms) {
            if ($forms[key]) {
                $forms[key].value = "";
            }
        }
    };

    $openModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);
    $closeModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);

    const subwayLineId = () => {
        return $forms.subwayLineId.value;
    };

    const makeFrom = () => {
        return {
            name: $forms.subwayLineNameInput.value,
            color: $forms.subwayLineColorInput.value,
            startTime: $forms.subwayLineFirstTime.value,
            endTime: $forms.subwayLineLastTime.value,
            intervalTime: $forms.subwayLineIntervalTime.value
        };
    };

    const setBy = data => {
        $forms.subwayLineNameInput.value = data.name;
        $forms.subwayLineColorInput.value = data.color;
        $forms.subwayLineFirstTime.value = data.startTime;
        $forms.subwayLineLastTime.value = data.endTime;
        $forms.subwayLineIntervalTime.value = data.intervalTime;
        $forms.subwayLineId.value = data.id;
    };

    return {$changeInfo, toggle, subwayLineId, makeFrom, setBy};
}
