import { EVENT_TYPE, ERROR_MESSAGE, KEY_TYPE } from "../../utils/constants.js";
import { listItemTemplate } from "../../utils/templates.js";

function AdminStation() {
  const $stationInput = document.querySelector("#station-name");
  const $stationInputButton = document.querySelector("#station-add-btn");
  const $stationList = document.querySelector("#station-list");

  const onAddStationHandler = event => {
    if (event.key !== KEY_TYPE.ENTER && event.type !== EVENT_TYPE.CLICK) {
      return;
    }
    event.preventDefault();
    const $stationNameInput = document.querySelector("#station-name");
    const stationName = $stationNameInput.value;

    validate(stationName);

    let data = {
      name: stationName
    };

    fetch("/stations", {
      method: "POST",
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    }).then(response => response.json())
    .then(response=>{
      console.log(JSON.stringify(response));
    }).catch(error=>{
      alert(error);
    });

    $stationNameInput.value = "";
    $stationList.insertAdjacentHTML("beforeend", listItemTemplate(stationName));
  };

  function validate(stationName) {
    if (!stationName) {
      alert(ERROR_MESSAGE.NOT_EMPTY);
      throw new Error();
    }
    if (stationName.includes(" ")) {
      alert(ERROR_MESSAGE.NOT_BLANK);
      throw new Error();
    }
    //myCode
    if (/\d/.test(stationName)) {
      alert(ERROR_MESSAGE.CONTAIN_NUMBER);
      throw new Error();
    }
    //myCode
    if (duplicatedName(stationName)) {
      alert(ERROR_MESSAGE.DUPLICATE_NAME);
      throw new Error();
    }
  }

  function duplicatedName(input) {
    const names = document.querySelectorAll(".list-item");
    const namesArr = Array.from(names);
    return namesArr.some(element => {
      return element.innerText === input;
    });
  }

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      $target.closest(".list-item").remove();
    }
  };

  const initEventListeners = () => {
    $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    $stationInputButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
  };

  const init = () => {
    initEventListeners();
  };

  return {
    init
  };
}

const adminStation = new AdminStation();
const $stationList = document.querySelector("#station-list");
adminStation.init();
window.onload = async function(event) {
  const response = await fetch("/stations", {
      method: "GET",
      headers: {
        'Content-Type': 'application/json'
      }
    });
  const jsonResponse = await response.json();
  for(const station of jsonResponse) {
    $stationList.insertAdjacentHTML("beforeend", listItemTemplate(station.name));
  }
};
