import {
    listItemTemplate,
    optionTemplate,
    subwayLinesItemTemplate
} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {ERROR_MESSAGE, EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
    const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
    const $selectOptions = document.querySelector("#station-select-options");
    const $departStationInput = document.querySelector("#depart-station-name");
    const $arrivalStationInput = document.querySelector(
        "#arrival-station-name");
    const $submitEdgeBtn = document.querySelector("#submit-button");

    const createSubwayEdgeModal = new Modal();

    const resetModalInputValue = () => {
        $departStationInput.value = "";
        $arrivalStationInput.value = "";
    };

    const initSubwayLinesSlider = async () => {
        const lines = await api.line.get().then(data => data.json());
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
        const lines = await api.line.get().then(data => data.json());
        const subwayLineOptionTemplate = lines
        .map(line => optionTemplate(line))
        .join("");

        const $stationSelectOptions = document.querySelector(
            "#station-select-options"
        );
        $stationSelectOptions.insertAdjacentHTML(
            "afterbegin",
            subwayLineOptionTemplate
        );
    };

    const onCreateEdgeHandler = async event => {
        event.preventDefault();
        const stations = await api.station.get().then(data => data.json());
        const depart = stations.filter(
            station => station.name === $departStationInput.value)[0];
        const arrival = stations.filter(
            station => station.name === $arrivalStationInput.value)[0];

        if (!arrival) {
            alert(ERROR_MESSAGE.NO_STATION);
            return;
        }

        const newEdge = {
            preStationId: depart ? depart.id : null,
            stationId: arrival.id,
            distance: 0,
            duration: 0
        };
        const lineId = $selectOptions.options[$selectOptions.selectedIndex].dataset.lineId;
        const $stationList = document.querySelector(`.station-list-${lineId}`);
        const lineStations = await api.edge.create(lineId, newEdge).then(
            data => data.json());
        const stationsTemplate = lineStations
        .map(station => listItemTemplate(station))
        .join("");
        $stationList.innerHTML = stationsTemplate;

        createSubwayEdgeModal.toggle();
        resetModalInputValue();
    }

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (!isDeleteButton) {
            return;
        }

        const lineId = $target.closest(".line-info-container").dataset.lineId;
        const $station = $target.closest(".list-item");
        const stationId = $station.dataset.stationId;

        api.edge.delete(lineId, stationId)
        .then(() => $station.remove());
    };

    const initEventListeners = () => {
        $submitEdgeBtn.addEventListener(
            EVENT_TYPE.CLICK,
            onCreateEdgeHandler
        );
        $subwayLinesSlider.addEventListener(
            EVENT_TYPE.CLICK,
            onRemoveStationHandler
        );
    };

    this.init = () => {
        initSubwayLinesSlider();
        initSubwayLineOptions();
        initEventListeners();
    };
}

const adminEdge = new AdminEdge();
adminEdge.init();

