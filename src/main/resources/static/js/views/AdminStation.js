import {
	CLICK_TYPE,
	CONFIRM_MESSAGE,
	ERROR_MESSAGE,
	EVENT_TYPE,
	KEY_TYPE
} from "../../utils/constants.js";
import { listItemTemplate } from "../../utils/templates.js";
import api from "../../api/index.js";

function AdminStation() {
	const $stationInput = document.querySelector("#station-name");
	const $stationList = document.querySelector("#station-list");
	const $stationAddButton = document.querySelector("#station-add-btn");
	let subwayStations = null;

	const isInvalidKey = event => {
		return (event.key !== KEY_TYPE.ENTER) && (event.button !== CLICK_TYPE.LEFT_CLICK);
	}

	const isEmpty = name => {
		if (!name) {
			alert(ERROR_MESSAGE.NOT_EMPTY);
			return true;
		}
		return false;
	};

	const hasSpace = name => {
		const pattern = /\s/g;

		if (pattern.test(name)) {
			alert(ERROR_MESSAGE.NOT_CONTAIN_SPACE);
			return true;
		}
		return false;
	};

	const hasNumber = name => {
		const pattern = /\d/g;

		if (pattern.test(name)) {
			alert(ERROR_MESSAGE.NOT_CONTAIN_NUMBER);
			return true;
		}
		return false;
	};

	const isInvalid = name => {
		return isEmpty(name) || hasSpace(name) || hasNumber(name);
	};

	const isDuplicate = name => {
		const isDuplicateName = subwayStations
			.map(station => station.name).includes(name);
		if (isDuplicateName) {
			alert(ERROR_MESSAGE.NOT_DUPLICATE);
			return true;
		}
		return false;
	};

	const onCreateSubwayStation = async event => {
		if (isInvalidKey(event)) {
			return;
		}
		event.preventDefault();
		const $stationNameInput = document.querySelector("#station-name");
		const stationName = $stationNameInput.value;
		$stationNameInput.value = "";
		if (isInvalid(stationName) || isDuplicate(stationName)) {
			return;
		}
		const station = {
			name: stationName
		}
		const createdStation = await api.station.create(station);
		$stationList.insertAdjacentHTML(
			"beforeend",
			listItemTemplate(createdStation)
		);
	};

	const isDeleteConfirmed = () => confirm(CONFIRM_MESSAGE.DELETE);

	const onDeleteSubwayStation = async event => {
		const $target = event.target;
		const $station = $target.closest(".list-item");
		const isDeleteButton = $target.classList.contains("mdi-delete");
		if (isDeleteButton && isDeleteConfirmed()) {
			$station.remove();
			await api.station.delete($station.dataset.stationId);
		}
	};

	const initEventListeners = () => {
		$stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onCreateSubwayStation);
		$stationAddButton.addEventListener(EVENT_TYPE.CLICK, onCreateSubwayStation);
		$stationList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayStation);
	};

	const initStationNames = async () => {
		subwayStations = await api.station.get();
		subwayStations.forEach(station => {
			$stationList.insertAdjacentHTML(
				"beforeend",
				listItemTemplate(station)
			);
		})
	}

	const init = () => {
		initEventListeners();
		initStationNames();
	};

	return {
		init
	};
}

const adminStation = new AdminStation();
adminStation.init();