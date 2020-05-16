import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE, HTTP_STATUS} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js"

function AdminEdge() {
    const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
    const $subwayLineAddButton = document.querySelector("#subway-line-add-btn");
    const $submitButton = document.querySelector('#submit-button');
    const createSubwayEdgeModal = new Modal();
    const lineFinder = new Map();

    const initSubwayLinesSlider = () => {
        api.line.get()
            .then(response => response.json())
            .then(subwayLines => {
                $subwayLinesSlider.innerHTML = subwayLines
                    .map(line => {
                            const subwayLine = {
                                id: line.id,
                                title: line.title,
                                bgColor: line.bgColor,
                                stations: line.stations
                            };
                            return subwayLinesItemTemplate(subwayLine);
                        }
                    )
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
            }
        );
    };

    const initSubwayLineOptions = () => {
        api.line.get()
            .then(response => response.json())
            .then(subwayLines => {
                lineFinder.clear();
                subwayLines.forEach(line => lineFinder.set(line.title, line.id));

                const subwayLineOptionTemplate = subwayLines
                    .map(line => {
                        return optionTemplate(line.title)
                    })
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

    const onSubwayLineAddBtnClicked = () => {
        $submitButton.classList.add('subway-line-add-button');
    };

    const onSubmitClicked = event => {
        event.preventDefault();
        const $selectOptions = document.querySelector("#station-select-options");
        const $selectOptionValue = $selectOptions.options[$selectOptions.selectedIndex].value;
        const $lineId = lineFinder.get($selectOptionValue);

        const isSubwayLineAddButton = event.target.classList.contains("subway-line-add-button");
        if (!isSubwayLineAddButton) {
            return;
        }

        const departStationName = document.querySelector("#depart-station-name").value;
        const arrivalStationName = document.querySelector("#arrival-station-name").value;
        if (!arrivalStationName) {
            alert("대상역은 반드시 존재해야 합니다!");
            return;
        }
        const names = [departStationName, arrivalStationName].filter(name => name).join(",");
        const lineStations = "/?names=" + names;

        requestStations(lineStations, stations => {
            let preStationId = null;
            let stationId;
            if (stations.length === 1) {
                stationId = stations[0].id;
            } else {
                const isFirstStationDeparture = departStationName === stations[0].name;
                preStationId = stations[isFirstStationDeparture ? 0 : 1].id;
                stationId = stations[isFirstStationDeparture ? 1 : 0].id;
            }
            const lineStation = {
                preStationId,
                stationId,
                distance: 10,
                duration: 10
            };

            createLineStation($lineId, lineStation, () => {
                createSubwayEdgeModal.toggle();
                initSubwayLinesSlider();
            });
        });
    };

    const createLineStation = (lineId, lineStation, onCompleteCreateLineStation) => {
        api.line
            .registerLineStation(lineId, lineStation)
            .then(response => {
                if (response.status === HTTP_STATUS.CREATED) {
                    response.json().then(() => onCompleteCreateLineStation());
                } else {
                    response.text().then(errorMessage => alert(errorMessage));
                }
            })
    };

    const requestStations = (lineStations, callback) => {
        api.station.getStationsByNames(lineStations)
            .then(response => {
                if (response.status === HTTP_STATUS.PRECONDITION_FAILED) {
                    alert("존재하지 않는 역을 입력하셨습니다!");
                } else if (response.status === HTTP_STATUS.OK) {
                    response.json().then(result => callback(result));
                }
            });
    };

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            const $stationId = $target.dataset.stationId;
            const $lineId = $target.parentNode.parentNode.parentNode.parentNode.firstElementChild.dataset.lineId;
            $target.closest(".list-item").remove();
            api.line.deleteLineStation($lineId, $stationId).then();
        }
    };

    const initEventListeners = () => {
        $subwayLinesSlider.addEventListener(
            EVENT_TYPE.CLICK,
            onRemoveStationHandler
        );
        $subwayLineAddButton.addEventListener(
            EVENT_TYPE.CLICK,
            onSubwayLineAddBtnClicked
        );
        $submitButton.addEventListener(
            EVENT_TYPE.CLICK,
            onSubmitClicked
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
