import {listItemTemplate, optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
    const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
    const $selectStation = document.querySelector("#station-select-options");
    const $preStation = document.querySelector("#depart-station-name");
    const $currentStation = document.querySelector("#arrival-station-name");
    const $createLineStationButton = document.querySelector("#submit-button");
    const $closeSubwayLineStationButton = document.querySelector(".modal-close");

    const createSubwayEdgeModal = new Modal();

    const onCreateSubwayEdge = async event => {
        event.preventDefault();

        let preStationId;
        let currentStationId;

        await api.station.getId($preStation.value).then(data => {
            preStationId = data;
        });
        await api.station.getId($currentStation.value).then(data => {
            currentStationId = data;
        });

        const lineId = $selectStation.options[$selectStation.selectedIndex].dataset.edgeId;

        const newSubwayEdge = {
            preStationId: preStationId,
            stationId: currentStationId,
            distance: 10,
            duration: 10
        }

        api.lines.createLineStation(lineId, newSubwayEdge).then(data => {
            let sameLines = document.querySelectorAll(`[data-line-edge-id="${data.id}"]`);
            sameLines.forEach(line => {
                    line.lastElementChild.innerHTML = data.stations.map(station => listItemTemplate(station)).join("");
                }
            );
        });

        createSubwayEdgeModal.toggle();
        onEmptyInput();
    }

    const initSubwayLinesSlider = async () => {
        await api.lines.get().then(data => {
            $subwayLinesSlider.innerHTML = data.map(line => subwayLinesItemTemplate(line)).join("");
        });

        tns({
            container: ".subway-lines-slider",
            loop: true,
            slideBy: "page",
            speed: 400,
            autoplayButtonOutput: false,
            mouseDrag: true,
            lazyload: true,
            controlsContainer: "#slider-controls",
            items: 1,
            edgePadding: 25
        });
    };

    const initSubwayLineOptions = async () => {
        let subwayLineOptionTemplate;
        await api.lines.get().then(data => {
            subwayLineOptionTemplate = data.map(line => optionTemplate(line)).join("");
        })

        const $stationSelectOptions = document.querySelector(
            "#station-select-options"
        );
        $stationSelectOptions.insertAdjacentHTML(
            "afterbegin",
            subwayLineOptionTemplate
        );
    };

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            let lineId = $target.closest(".line-templates").dataset.lineEdgeId;
            let stationId = $target.closest(".list-item").dataset.stationId;

            $target.closest(".list-item").remove();
            api.lines.deleteLineStation(lineId, stationId);

        }
    };

    const onEmptyInput = () => {
        $selectStation.value = "";
        $preStation.value = "";
        $currentStation.value = "";
    }

    const initEventListeners = () => {
        $subwayLinesSlider.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
        $createLineStationButton.addEventListener(EVENT_TYPE.CLICK, onCreateSubwayEdge);
        $closeSubwayLineStationButton.addEventListener(EVENT_TYPE.CLICK, onEmptyInput);
    };

    this.init = () => {
        initSubwayLinesSlider();
        initSubwayLineOptions();
        initEventListeners();
    };
}

const adminEdge = new AdminEdge();
adminEdge.init();
