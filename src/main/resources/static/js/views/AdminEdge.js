import {
    optionTemplate,
    stationOptionTemplate,
    subwayLinesItemTemplate
} from "../../utils/templates.js";
import {defaultSubwayLines} from "../../utils/subwayMockData.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
    const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
    const $subwayLineStationSubmitButton = document.querySelector("#submit-button");
    const $subwayLineSelection = document.querySelector("#line-select-options");
    const $subwayDepartStation = document.querySelector("#pre-station-select-options");
    const $subwayArrivalStation = document.querySelector("#destination-station-select-options");
    const createSubwayEdgeModal = new Modal();

    const initSubwayLinesSlider = async () => {
        const lines = await api.line.get();
        $subwayLinesSlider.innerHTML = lines
            .map(line => subwayLinesItemTemplate(line))
            .join("");
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
        const lines = await api.line.get();
        const subwayLineOptionTemplate = lines
            .map(line => optionTemplate(line))
            .join("");
        const $lineSelectOptions = document.querySelector(
            "#line-select-options"
        );
        $lineSelectOptions.insertAdjacentHTML(
            "afterbegin",
            subwayLineOptionTemplate
        );
    };

    const initSubwayStationsOptions = async () => {
        const stations = await api.station.get()
        const subwayLineOptionTemplate = stations
            .map(station => stationOptionTemplate(station))
            .join("");
        const $preStationSelectOptions = document.querySelector(
            "#pre-station-select-options"
        );
        const $destinationStationSelectOptions = document.querySelector(
            "#destination-station-select-options"
        );
        $preStationSelectOptions.insertAdjacentHTML(
            "afterbegin",
            subwayLineOptionTemplate
        );
        $destinationStationSelectOptions.insertAdjacentHTML(
            "afterbegin",
            subwayLineOptionTemplate
        );
    };

    const onCreateSubwayLineStation = async () => {
        const selectedLineIndex = $subwayLineSelection.selectedIndex;
        const selectedArrivalStationIndex = $subwayArrivalStation.selectedIndex;
        const selectedDepartStationIndex = $subwayDepartStation.selectedIndex;
        const lineStation = {
            preStationId: $subwayDepartStation.options[selectedDepartStationIndex].dataset.stationId,
            stationId: $subwayArrivalStation.options[selectedArrivalStationIndex].dataset.stationId,
            distance: 1000,
            duration: 5
        }
        const selectedLineId = $subwayLineSelection.options[selectedLineIndex].dataset.lineId;
        console.log(lineStation);
        await api.lineStation.update(lineStation, selectedLineId);
    }

    const onRemoveStationHandler = async event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            const selectedLineId = $target.closest("#line-name").dataset.lineId;
            const selectedStationId = $target.closest(".list-item").dataset.stationId;
            $target.closest(".list-item").remove();
            await api.lineStation.delete(selectedLineId, selectedStationId);
        }
    };

    const initEventListeners = () => {
        $subwayLinesSlider.addEventListener(
            EVENT_TYPE.CLICK,
            onRemoveStationHandler
        );
        $subwayLineStationSubmitButton.addEventListener(EVENT_TYPE.CLICK, onCreateSubwayLineStation);
    };

    this.init = () => {
        initSubwayLinesSlider();
        initSubwayLineOptions();
        initSubwayStationsOptions();
        initEventListeners();
    };
}

const adminEdge = new AdminEdge();
adminEdge.init();
