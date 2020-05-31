import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {ERROR_MESSAGE, EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";

function AdminEdge() {
    const $subwayLinesSlider = document.querySelector(".subway-lines-slider");

    const $departStationNameInput = document.querySelector("#depart-station-name");
    const $arriveStationNameInput = document.querySelector("#arrival-station-name");
    const $distance = document.querySelector("#distance");
    const $duration = document.querySelector("#duration");
    const $createButton = document.querySelector("#submit-button");
    const createSubwayEdgeModal = new Modal();
    let stations = null;
    let preStationId = null;
    let stationId = null;

    const createLineStation = (event) => {
        if (!$arriveStationNameInput.value) {
            return alert(ERROR_MESSAGE.NOT_EMPTY_STATION_NAME);
        }

        stations.then(data => {
            for (let i = 0; i < data.length; i++) {
                if (data[i].name === $departStationNameInput.value) {
                    preStationId = data[i].id;
                    break;
                }
            }

            for (let i = 0; i < data.length; i++) {
                if (data[i].name === $arriveStationNameInput.value) {
                    stationId = data[i].id;
                    break;
                }
            }

            const $stationSelectOptions = document.querySelector(
                "#station-select-options"
            );

            let url = "/lines/" + $stationSelectOptions.value + "/station/";
            let createRequest = {
                headers: {
                    'Content-Type': 'application/json'
                },
                method: 'POST',
                body: JSON.stringify({
                    preStationId: preStationId,
                    stationId: stationId,
                    distance: $distance.value,
                    duration: $duration.value
                })
            };
            fetch(url, createRequest)
                .then(res => {
                        if (res.ok) {
                            res.json().then(data => {
                                $subwayLinesSlider.insertAdjacentHTML(
                                    "beforeend",
                                    subwayLinesItemTemplate(data)
                                )
                            })
                        } else {
                            alert("station을 업데이트 하지 못했습니다.");
                        }
                    }
                );
            createSubwayEdgeModal.toggle();
            $departStationNameInput.value = "";
            $arriveStationNameInput.value = "";
            $distance.value = "";
            $duration.value = "";
            preStationId = null;
            stationId = null;
        });
    };

    const initSubwayLinesSlider = () => {
        fetch("/lines", {
            method: 'get',
            headers: {
                'content-type': 'application/json'
            }
        }).then(res => {
            if (res.ok) {
                res.json().then(data => {
                    $subwayLinesSlider.innerHTML = data
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
                })
            } else {
                alert("Line 데이터를 불러올 수 없습니다.");
            }
        });
    };

    const initSubwayLineOptions = () => {
        fetch("/lines", {
            method: 'get',
            headers: {
                'content-type': 'application/json'
            }
        }).then(res => {
            if (res.ok) {
                res.json().then(data => {
                    const subwayLineOptionTemplate = data
                        .map(line => optionTemplate(line))
                        .join("");
                    const $stationSelectOptions = document.querySelector(
                        "#station-select-options"
                    );
                    $stationSelectOptions.insertAdjacentHTML(
                        "afterbegin",
                        subwayLineOptionTemplate
                    );
                })
            } else {
                alert("데이터를 불러올 수 없습니다.");
            }
        });
    };

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const $targetList = $target.closest(".list-item");
        const isDeleteButton = $target.classList.contains("mdi-delete");

        if (isDeleteButton && confirm("정말로 지우시겠습니까?")) {
            let lineId = $targetList.closest(".slider-list").id;
            stationId = $targetList.dataset.stationId;
            fetch("/lines/" + lineId + "/station/" + stationId, {
                method: 'delete'
            }).then(res => {
                if (res.ok) {
                    $target.closest(".list-item").remove();
                }
            });
        }
    };

    const initEventListeners = () => {
        $subwayLinesSlider.addEventListener(
            EVENT_TYPE.CLICK,
            onRemoveStationHandler
        );
        $createButton.addEventListener(EVENT_TYPE.CLICK, createLineStation);
    };

    const initStations = () => {
        stations = fetch("/stations", {
            method: 'get',
            headers: {
                'content-type': 'application/json'
            }
        }).then(res => {
            if (res.ok) {
                return res.json();
            } else {
                alert("지하철역을 불러오지 못헀습니다.");
            }
        });
    };

    this.init = () => {
        initStations();
        initSubwayLinesSlider();
        initSubwayLineOptions();
        initEventListeners();
    };
}

const adminEdge = new AdminEdge();
adminEdge.init();
