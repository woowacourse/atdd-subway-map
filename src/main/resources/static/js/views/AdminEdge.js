import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
    const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
    const $createSubwayEdgeButton = document.querySelector(
        "#subway-edge-create-form #submit-button"
    );

    const subwayEdgeModal = new Modal();

    const onCreateSubwayEdge = event => {
        event.preventDefault();

        const lineId = document.querySelector("#line-select-options").selectedOptions[0].dataset.id;
        const beforeStationId = document.querySelector("#before-station-select-options").selectedOptions[0].dataset.id;
        const afterStationId = document.querySelector("#after-station-select-options").selectedOptions[0].dataset.id;
        const duration = document.querySelector("#duration").value;
        const distance = document.querySelector("#distance").value;

        const data = {
            preStationId: beforeStationId,
            stationId: afterStationId,
            distance: distance,
            duration: duration
        };

        api.edge.create(lineId, data)
            .then(response => {
                api.edge.findByLineId(lineId)
                    .then(response => {
                        initSubwayLinesSlider();
                        subwayEdgeModal.toggle(event);
                    })
                    .catch(alert);
            })
            .catch(alert);
    };

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");

        if (isDeleteButton) {
            const deleteButton = $target.closest(".mdi-delete");
            const stationId = deleteButton.dataset.id;
            const lineId = deleteButton.dataset.lineId;

            const data = {};
            data.stationId = stationId;

            api.edge.delete(lineId, data)
                .then(response => $target.closest(".list-item").remove())
                .catch(alert);
        }
    };

    const initSubwayLinesSlider = () => {
        api.line.get()
            .then(response => {
                $subwayLinesSlider.innerHTML = response.body
                    .map(line => subwayLinesItemTemplate(line))
                    .join("");
            })
            .catch(alert)
            .finally(() => {
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
        api.line.get()
            .then(response => {
                const subwayLineOptionTemplate = response.body
                    .map(line => optionTemplate(line.title, line.id))
                    .join("");
                const $lineSelectOptions = document.querySelector(
                    "#line-select-options"
                );
                $lineSelectOptions.insertAdjacentHTML(
                    "afterbegin",
                    subwayLineOptionTemplate
                );
            });
    };

    const initSubwayStationOptions = () => {
        api.station.get()
            .then(response => {
                const subwayStationOptionTemplate = response.body
                    .map(station => optionTemplate(station.name, station.id))
                    .join("");
                const $beforeStationSelectOptions = document.querySelector(
                    "#before-station-select-options"
                );
                const $afterStationtationSelectOptions = document.querySelector(
                    "#after-station-select-options"
                );
                $beforeStationSelectOptions.insertAdjacentHTML(
                    "beforeend",
                    subwayStationOptionTemplate
                );
                $afterStationtationSelectOptions.insertAdjacentHTML(
                    "beforeend",
                    subwayStationOptionTemplate
                );
            })
    };

    const initEventListeners = () => {
        $subwayLinesSlider.addEventListener(
            EVENT_TYPE.CLICK,
            onRemoveStationHandler
        );
        $createSubwayEdgeButton.addEventListener(
            EVENT_TYPE.CLICK,
            onCreateSubwayEdge);
    };

    this.init = () => {
        initSubwayLinesSlider();
        initSubwayLineOptions();
        initEventListeners();
        initSubwayStationOptions();
    };
}

const adminEdge = new AdminEdge();
adminEdge.init();
