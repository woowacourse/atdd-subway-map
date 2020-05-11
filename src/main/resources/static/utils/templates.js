export const listItemTemplate = station =>
    `<div class="list-item border border-gray-200 py-2 px-4 text-gray-800" data-station-id="${station.id}">
    ${station.name}
    <button class="hover:bg-gray-300 hover:text-gray-500 text-gray-300 px-1 rounded-full float-right">
       <span class="mdi mdi-delete"></span>
    </button>
  </div>`;

export const subwayLineInfoTemplate = line =>
    `<div class="w-1/2 p-2 text-center text-gray-800 bg-gray-200">첫차 시간</div>
     <div class="w-1/2 p-2 text-center text-gray-800 bg-gray-100">${line.startTime}</div>
     <div class="w-1/2 p-2 text-center text-gray-800 bg-gray-200">막차 시간</div>
     <div class="w-1/2 p-2 text-center text-gray-800 bg-gray-100">${line.endTime}</div>
     <div class="w-1/2 p-2 text-center text-gray-800 bg-gray-200">간격</div>
     <div class="w-1/2 p-2 text-center text-gray-800 bg-gray-100">${line.intervalTime}</div>`;


export const subwayLinesTemplate = line =>
    `<div class="subway-line-item border border-gray-200 py-2 px-4 text-gray-800" data-line-id="${line.id}">
      <span class="${line.bgColor} w-3 h-3 rounded-full inline-block mr-1"></span>
      ${line.name}
      <button class="hover:bg-gray-300 hover:text-gray-500 text-gray-300 px-1 rounded-full float-right">
         <span class="mdi mdi-delete"></span>
      </button>
      <button class="hover:bg-gray-300 hover:text-gray-500 text-gray-300 px-1 rounded-full float-right">
         <span class="mdi mdi-pencil"></span>
      </button>
    </div>`;

export const optionTemplate = line => `<option data-line-id="${line.id}">${line.name}</option>`;

const navTemplate = `<nav class="flex items-center justify-between flex-wrap bg-yellow-500 p-4">
  <div class="flex items-center flex-shrink-0 text-gray-800 w-full">
      <a href="/" class="mr-2">
        <img src="../images/logo_small.png" class="w-6">
      </a>
    <div class="flex justify-start">
      <div class="hover:bg-yellow-400 px-2 py-1 rounded">
         <a href="/admin-station.html" class="block inline-block lg:mt-0 text-gray-800 text-sm">
          역 관리
          </a>
      </div>
      <div class="hover:bg-yellow-400 px-2 py-1 rounded">
         <a href="/admin-line.html" class="block inline-block lg:mt-0 text-gray-800 text-sm">
          노선 관리
          </a>
      </div>
      <div class="hover:bg-yellow-400 px-2 py-1 rounded">
          <a href="/admin-edge.html" class="block inline-block lg:mt-0 text-gray-800 text-sm">
          구간 관리
          </a>
      </div>
    </div>
</nav>`;

export const subwayLinesItemTemplate = line => {
    const stationsTemplate = line.stations
        .map(station => listItemTemplate(station))
        .join("");
    return `<div class="line-info-container inline-block w-1/2 px-2" data-line-id="${line.id}">
            <div class="rounded-sm w-full slider-list">
              <div class="border ${line.bgColor} lint-title px-4 py-1">${line.name}</div>
              <div class="line-station-list-container overflow-y-auto height-90 station-list-${line.id}">
              ${stationsTemplate}
              </div>
            </div>
          </div>`;
};


export const initNavigation = () => {
    document.querySelector("body").insertAdjacentHTML("afterBegin", navTemplate);
};

export const colorSelectOptionTemplate = (option, index) => {
    const hasNewLine = ++index % 7 === 0;

    return ` <button data-color="${
        option.bgColor
    }" class="color-select-option button w-6 h-6 ${option.bgColor} ${
        option.hoverColor
    } font-bold p-1 rounded ">
             </button> ${hasNewLine ? "<br/>" : ""}`;
};

