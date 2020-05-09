import {
    emptyOptionTemplate,
    lineOptionTemplate,
    stationOptionTemplate,
    subwayLinesItemTemplate
} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import api from "../../api/index.js";
import Modal from "../../ui/Modal.js";

function AdminEdge() {
    const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
    const createSubwayEdgeModal = new Modal();

    const initSubwayLinesSlider = (lines) => {
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

    const initLineOptions = () => {
        api.line
            .get()
            .then(lines => {
                const linesOptionTemplate = lines
                    .map(line => lineOptionTemplate(line))
                    .join("");

                const $lineSelectOptions = document.querySelector(
                    "#line-select-options"
                );
                $lineSelectOptions.insertAdjacentHTML(
                    "afterbegin",
                    linesOptionTemplate
                );
            });
    };

    const initStationOptions = () => {
        api.station
            .get()
            .then(stations => {
                const stationsOptionTemplate = stations
                    .map(station => stationOptionTemplate(station))
                    .join("");

                const $stationSelectOptions = document.querySelectorAll(
                    ".station-select-options"
                );
                $stationSelectOptions[0].insertAdjacentHTML("afterbegin", emptyOptionTemplate());
                $stationSelectOptions.forEach(select => {
                    select.insertAdjacentHTML(
                        "beforeend",
                        stationsOptionTemplate
                    );
                });
            });
    };

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            $target.closest(".list-item").remove();
        }
    };

    const initEventListeners = () => {
        $subwayLinesSlider.addEventListener(
            EVENT_TYPE.CLICK,
            onRemoveStationHandler
        );
    };

    const initLineStation = () => {
        api.lineStation
            .get()
            .then(linesStations => initSubwayLinesSlider(linesStations));
    };

    this.init = () => {
        initLineStation();
        initLineOptions();
        initStationOptions();
        initEventListeners();
    };
}

const adminEdge = new AdminEdge();
adminEdge.init();
