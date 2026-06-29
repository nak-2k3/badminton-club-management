if ("scrollRestoration" in history) {
  history.scrollRestoration = "auto";
}

/* =========================
   DISABLE HTMX NAVIGATION
========================= */

function disableHtmxNavigation() {
  if (!document.body) return;

  document.body.setAttribute("hx-boost", "false");
  document.body.removeAttribute("hx-target");
  document.body.removeAttribute("hx-select");
  document.body.removeAttribute("hx-push-url");
  document.body.removeAttribute("hx-swap");

  document.querySelectorAll("[hx-boost]").forEach((element) => {
    element.setAttribute("hx-boost", "false");
  });
}

/* =========================
   DOM HELPERS
========================= */

function getMainContent() {
  return document.getElementById("main-content");
}

function addPageLoading() {
  const mainContent = getMainContent();

  if (mainContent) {
    mainContent.classList.add("is-page-loading");
  }
}

function removePageLoading() {
  const mainContent = getMainContent();

  if (mainContent) {
    mainContent.classList.remove("is-page-loading");
  }
}

/* =========================
   NAVBAR ACTIVE
========================= */

function setActiveNavbar() {
  const currentPath = window.location.pathname;

  document
    .querySelectorAll(".app-nav-link, .app-dropdown .dropdown-item")
    .forEach((link) => {
      const href = link.getAttribute("href");

      if (!href || href === "#") return;

      link.classList.remove("active");

      if (href === "/" && currentPath === "/") {
        link.classList.add("active");
        return;
      }

      if (href !== "/" && currentPath.startsWith(href)) {
        link.classList.add("active");

        const dropdown = link.closest(".dropdown");

        if (dropdown) {
          const dropdownToggle = dropdown.querySelector(".app-nav-link");

          if (dropdownToggle) {
            dropdownToggle.classList.add("active");
          }
        }
      }
    });
}

function closeNavbarMenu() {
  const navbarCollapse = document.querySelector(".navbar-collapse.show");

  if (navbarCollapse && window.bootstrap) {
    const collapse = bootstrap.Collapse.getOrCreateInstance(navbarCollapse);
    collapse.hide();
  }
}

/* =========================
   CONFIRM
========================= */

function initConfirmActions() {
  if (document.body.dataset.confirmGlobalBound === "true") return;

  document.body.dataset.confirmGlobalBound = "true";

  document.addEventListener(
    "click",
    function (event) {
      const element = event.target.closest("[data-confirm]");

      if (!element) return;

      const message = element.getAttribute("data-confirm");

      if (!message) return;

      const accepted = confirm(message);

      if (!accepted) {
        event.preventDefault();
        event.stopPropagation();
        event.stopImmediatePropagation();
        return false;
      }
    },
    true,
  );

  document.addEventListener(
    "submit",
    function (event) {
      const form = event.target;

      if (!form || !form.matches("form[data-confirm]")) return;

      const message = form.getAttribute("data-confirm");

      if (!message) return;

      const accepted = confirm(message);

      if (!accepted) {
        event.preventDefault();
        event.stopPropagation();
        event.stopImmediatePropagation();
        return false;
      }
    },
    true,
  );
}

/* =========================
   FORM LOADING
========================= */

function initSubmitLoading() {
  document
    .querySelectorAll("form[data-disable-submit='true']")
    .forEach((form) => {
      if (form.dataset.submitLoadingBound === "true") return;

      form.dataset.submitLoadingBound = "true";

      form.addEventListener("submit", function () {
        const submitButton = form.querySelector("button[type='submit']");

        if (!submitButton) return;

        submitButton.disabled = true;

        if (!submitButton.dataset.originalText) {
          submitButton.dataset.originalText = submitButton.innerHTML;
        }

        submitButton.innerHTML = "Đang xử lý...";
      });
    });
}

/* =========================
   FLATPICKR AUTO LOAD
========================= */

const FLATPICKR_CSS_URL =
  "https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css";

const FLATPICKR_JS_URL = "https://cdn.jsdelivr.net/npm/flatpickr";

const FLATPICKR_MONTH_CSS_URL =
  "https://cdn.jsdelivr.net/npm/flatpickr/dist/plugins/monthSelect/style.css";

const FLATPICKR_MONTH_JS_URL =
  "https://cdn.jsdelivr.net/npm/flatpickr/dist/plugins/monthSelect/index.js";

let flatpickrLoadingPromise = null;
let flatpickrMonthLoadingPromise = null;

function loadStyleOnce(id, href) {
  if (document.getElementById(id)) return;

  const link = document.createElement("link");
  link.id = id;
  link.rel = "stylesheet";
  link.href = href;

  document.head.appendChild(link);
}

function loadScriptOnce(id, src, isLoaded) {
  return new Promise((resolve, reject) => {
    if (isLoaded && isLoaded()) {
      resolve();
      return;
    }

    const existingScript = document.getElementById(id);

    if (existingScript) {
      if (existingScript.dataset.loaded === "true") {
        resolve();
        return;
      }

      existingScript.addEventListener("load", function () {
        existingScript.dataset.loaded = "true";
        resolve();
      });

      existingScript.addEventListener("error", reject);
      return;
    }

    const script = document.createElement("script");
    script.id = id;
    script.src = src;
    script.async = true;

    script.onload = function () {
      script.dataset.loaded = "true";
      resolve();
    };

    script.onerror = reject;

    document.body.appendChild(script);
  });
}

function ensureFlatpickrLoaded() {
  if (window.flatpickr) {
    return Promise.resolve();
  }

  if (flatpickrLoadingPromise) {
    return flatpickrLoadingPromise;
  }

  loadStyleOnce("flatpickr-css", FLATPICKR_CSS_URL);

  flatpickrLoadingPromise = loadScriptOnce(
    "flatpickr-js",
    FLATPICKR_JS_URL,
    function () {
      return window.flatpickr;
    },
  );

  return flatpickrLoadingPromise;
}

function ensureFlatpickrMonthLoaded() {
  if (window.monthSelectPlugin) {
    return Promise.resolve();
  }

  if (flatpickrMonthLoadingPromise) {
    return flatpickrMonthLoadingPromise;
  }

  loadStyleOnce("flatpickr-month-css", FLATPICKR_MONTH_CSS_URL);

  flatpickrMonthLoadingPromise = ensureFlatpickrLoaded().then(function () {
    return loadScriptOnce(
      "flatpickr-month-js",
      FLATPICKR_MONTH_JS_URL,
      function () {
        return window.monthSelectPlugin;
      },
    );
  });

  return flatpickrMonthLoadingPromise;
}

function initDatePickers() {
  const dateInputs = document.querySelectorAll(".date-picker");
  const dateTimeInputs = document.querySelectorAll(".datetime-picker");
  const monthInputs = document.querySelectorAll(".month-picker");

  if (
    dateInputs.length === 0 &&
    dateTimeInputs.length === 0 &&
    monthInputs.length === 0
  ) {
    return;
  }

  if (dateInputs.length > 0 || dateTimeInputs.length > 0) {
    ensureFlatpickrLoaded()
      .then(function () {
        dateInputs.forEach((input) => {
          if (input._flatpickr) return;

          flatpickr(input, {
            dateFormat: "d/m/Y",
            allowInput: true,
          });
        });

        dateTimeInputs.forEach((input) => {
          if (input._flatpickr) return;

          flatpickr(input, {
            enableTime: true,
            time_24hr: true,
            minuteIncrement: 15,
            altInput: true,
            altFormat: "d/m/Y H:i",
            dateFormat: "Y-m-d\\TH:i",
            allowInput: false,
          });
        });
      })
      .catch(function () {
        console.error("Không thể tải Flatpickr.");
      });
  }

  if (monthInputs.length > 0) {
    ensureFlatpickrMonthLoaded()
      .then(function () {
        monthInputs.forEach((input) => {
          if (input._flatpickr) return;

          flatpickr(input, {
            altInput: true,
            allowInput: false,
            dateFormat: "Y-m",
            altFormat: "m/Y",
            plugins: [
              new monthSelectPlugin({
                shorthand: false,
                dateFormat: "Y-m",
                altFormat: "m/Y",
                theme: "light",
              }),
            ],
          });
        });
      })
      .catch(function () {
        console.error("Không thể tải Flatpickr Month Picker.");
      });
  }
}

/* =========================
   PAYMENT DETAIL FILTER
========================= */

function initPaymentDetailFilter() {
  const searchInput = document.getElementById("paymentSearchInput");
  const statusFilter = document.getElementById("paymentStatusFilter");
  const resetButton = document.getElementById("paymentResetFilter");
  const noResultMessage = document.getElementById("paymentNoResultMessage");

  if (!searchInput || !statusFilter) return;

  const rows = document.querySelectorAll(".payment-row");

  function filterPayments() {
    const keyword = searchInput.value.toLowerCase().trim();
    const selectedStatus = statusFilter.value;
    let visibleCount = 0;

    rows.forEach((row) => {
      const name = (row.getAttribute("data-name") || "").toLowerCase();
      const rowStatus = row.getAttribute("data-status") || "";

      const matchName = name.includes(keyword);
      const matchStatus =
        selectedStatus === "ALL" || rowStatus === selectedStatus;

      if (matchName && matchStatus) {
        row.style.display = "";
        visibleCount++;
      } else {
        row.style.display = "none";
      }
    });

    if (noResultMessage) {
      if (visibleCount === 0 && rows.length > 0) {
        noResultMessage.classList.remove("d-none");
      } else {
        noResultMessage.classList.add("d-none");
      }
    }
  }

  if (searchInput.dataset.paymentFilterBound !== "true") {
    searchInput.dataset.paymentFilterBound = "true";
    searchInput.addEventListener("input", filterPayments);
  }

  if (statusFilter.dataset.paymentFilterBound !== "true") {
    statusFilter.dataset.paymentFilterBound = "true";
    statusFilter.addEventListener("change", filterPayments);
  }

  if (resetButton && resetButton.dataset.paymentFilterBound !== "true") {
    resetButton.dataset.paymentFilterBound = "true";

    resetButton.addEventListener("click", function () {
      searchInput.value = "";
      statusFilter.value = "ALL";
      filterPayments();
    });
  }

  filterPayments();
}

/* =========================
   DROPDOWN INSIDE TABLE
========================= */

function initDropdownInsideTable() {
  document.querySelectorAll(".table .dropdown-toggle").forEach((button) => {
    button.setAttribute("hx-boost", "false");
  });
}

/* =========================
   INIT
========================= */

function initAppUI() {
  disableHtmxNavigation();
  setActiveNavbar();
  initConfirmActions();
  initSubmitLoading();
  initDatePickers();
  initPaymentDetailFilter();
  initDropdownInsideTable();
}

disableHtmxNavigation();

document.addEventListener("DOMContentLoaded", function () {
  initAppUI();
});

window.addEventListener("pageshow", function () {
  initAppUI();
});
