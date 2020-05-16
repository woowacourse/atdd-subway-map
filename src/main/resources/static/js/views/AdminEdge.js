import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
    const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
    const $lineSelectOptions = document.querySelector('#line-select-options');
    const $subwayLineAddButton = document.querySelector('#subway-line-add-btn');
    const $departStationOptions = document.querySelector('#depart-station-options');
    const $ArrivalStationOptions = document.querySelector('#arrival-station-options');
    const createSubwayEdgeModal = new Modal();

    let subwayLines = [];

    const initCreateEdgeForm = event => {
        event.preventDefault()
        initLineOptions(subwayLines)
        initPreviousStationOptions()
        initNextStationOptions()
    }

    const initLineOptions = subwayLines => {
        const subwayLineOptionTemplate = subwayLines
            .map(line => optionTemplate(line))
            .join("");
        $lineSelectOptions.innerHTML = subwayLineOptionTemplate;
    };

    const initPreviousStationOptions = () => {
        api.line.getDetail($lineSelectOptions.options[$lineSelectOptions.selectedIndex].dataset.id).then(line => {
            const stations = line.stations ? line.stations : []
            if (stations.length > 0) {
                $departStationOptions.innerHTML = stations.map(station => optionTemplate(station)).join('')
            }
        })
    }

    const initNextStationOptions = () => {
        api.station
            .get()
            .then(stations => {
                $ArrivalStationOptions.innerHTML = stations.map(station => optionTemplate(station)).join('')
            })
            .catch(() => alert(ERROR_MESSAGE.COMMON))
    }


    const initSubwayLinesSlider = () => {
        api.line.get()
            .then(data => {
                subwayLines = data;
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
            })
    };

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            $target.closest(".list-item").remove();
        }
    };

    const initEventListeners = () => {
        $subwayLinesSlider.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
        $subwayLineAddButton.addEventListener(EVENT_TYPE.CLICK, initCreateEdgeForm)
    };

    this.init = async () => {
        await initSubwayLinesSlider();
        await initLineOptions(subwayLines);
        await initEventListeners();
    };
}

const adminEdge = new AdminEdge();
adminEdge.init();
