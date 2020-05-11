import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE, KEY_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";


function AdminEdge() {
    const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
    const $createLineStationButton = document.querySelector("#submit-button");
    const $stationSelectOptions = document.querySelector("#station-select-options");
    const $arrivalStationName = document.querySelector("#arrival-station-name");
    const $departStationName = document.querySelector("#depart-station-name");

    const createSubwayEdgeModal = new Modal();

    const initSubwayLinesSlider = () => {
        api.line.get().then(data => {
            $subwayLinesSlider.innerHTML = data.map(line => subwayLinesItemTemplate(line))
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
        });
    };

    const initSubwayLineOptions = () => {
        api.line.get().then(data => {
            const subwayLineOptionTemplate = data.map(line => optionTemplate(line))
                .join("");
            const $stationSelectOptions = document.querySelector(
                "#station-select-options"
            );
            $stationSelectOptions.insertAdjacentHTML(
                "afterbegin",
                subwayLineOptionTemplate
            );
        });
    };

    const onCreateLineStationHandler = async event => {
        if (event.type !== EVENT_TYPE.CLICK && event.key !== KEY_TYPE.ENTER) {
            return;
        }

        event.preventDefault();
        const stations = await api.station.get();
        const preStation = stations.find(station => station.name === $departStationName.value);
        const station = stations.find(station => station.name === $arrivalStationName.value);
        const request = {
            preStationId: preStation ? preStation.id : null,
            stationId: station ? station.id : null,
            distance: 2,
            duration: 2
        };

        api.lineStation.create($stationSelectOptions.options[$stationSelectOptions.selectedIndex].dataset.id, request)
            .then(() => {
            createSubwayEdgeModal.toggle();
            initSubwayLinesSlider();
        })
    };

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            const lineId = $target.closest(".slider-list").dataset.id;
            const stationId = $target.closest(".list-item").dataset.id;

            api.lineStation.delete(lineId, stationId).then(() => {
                $target.closest(".list-item").remove();
            });
        }
    };

    const initEventListeners = () => {
        $subwayLinesSlider.addEventListener(
            EVENT_TYPE.CLICK,
            onRemoveStationHandler
        );
        $createLineStationButton.addEventListener(
            EVENT_TYPE.CLICK,
            onCreateLineStationHandler
        );
        $arrivalStationName.addEventListener(
            EVENT_TYPE.KEY_PRESS,
            onCreateLineStationHandler
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
