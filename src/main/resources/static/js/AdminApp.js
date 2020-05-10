import { initNavigation } from "../utils/templates.js";

function AdminApp() {
  const init = () => {
    console.log("adminapp");
    initNavigation();
  };

  return {
    init
  };
}

const adminApp = new AdminApp();
adminApp.init();
