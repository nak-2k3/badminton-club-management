if ("scrollRestoration" in history) {
  history.scrollRestoration = "manual";
}

/* =========================
   DOM HELPERS
========================= */

function getMainContent() {
  return document.getElementById("main-content");
}

function scrollMainToTop() {
  const mainContent = getMainContent();

  if (mainContent) {
    mainContent.scrollTop = 0;
  }

  window.scrollTo(0, 0);
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
  document.querySelectorAll("[data-confirm]").forEach((element) => {
    if (element.dataset.confirmBound === "true") return;

    element.dataset.confirmBound = "true";

    element.addEventListener("click", function (event) {
      const message = element.getAttribute("data-confirm");

      if (message && !confirm(message)) {
        event.preventDefault();
        event.stopPropagation();
      }
    });
  });
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
  setActiveNavbar();
  initConfirmActions();
  initSubmitLoading();
  initDatePickers();
  initPaymentDetailFilter();
  initDropdownInsideTable();
}

document.addEventListener("DOMContentLoaded", function () {
  initAppUI();
});

/* =========================
   HTMX EVENTS
========================= */

document.body.addEventListener("htmx:beforeRequest", function () {
  closeNavbarMenu();
  addPageLoading();
});

document.body.addEventListener("htmx:afterSwap", function (event) {
  initAppUI();

  if (event.detail.target && event.detail.target.id === "main-content") {
    scrollMainToTop();
  }
});

document.body.addEventListener("htmx:afterSettle", function () {
  removePageLoading();
});

document.body.addEventListener("htmx:responseError", function (event) {
  removePageLoading();

  const status = event.detail.xhr.status;
  const url = event.detail.pathInfo
    ? event.detail.pathInfo.requestPath
    : window.location.href;

  alert(
    "Có lỗi khi tải trang.\n\n" +
      "Mã lỗi: " +
      status +
      "\n" +
      "Đường dẫn: " +
      url +
      "\n\n" +
      "Bạn hãy xem log Spring Boot trong Terminal để biết lỗi chi tiết.",
  );
});

document.body.addEventListener("htmx:sendError", function () {
  removePageLoading();
  alert("Không thể gửi yêu cầu đến máy chủ. Vui lòng kiểm tra lại kết nối.");
});
