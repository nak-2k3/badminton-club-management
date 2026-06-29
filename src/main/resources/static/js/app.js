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

let flatpickrLoadingPromise = null;

function loadStyleOnce(id, href) {
  if (document.getElementById(id)) return;

  const link = document.createElement("link");
  link.id = id;
  link.rel = "stylesheet";
  link.href = href;

  document.head.appendChild(link);
}

function loadScriptOnce(id, src) {
  return new Promise((resolve, reject) => {
    const existingScript = document.getElementById(id);

    if (existingScript) {
      existingScript.addEventListener("load", resolve);
      existingScript.addEventListener("error", reject);

      if (window.flatpickr) {
        resolve();
      }

      return;
    }

    const script = document.createElement("script");
    script.id = id;
    script.src = src;
    script.async = true;

    script.onload = resolve;
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

  flatpickrLoadingPromise = loadScriptOnce("flatpickr-js", FLATPICKR_JS_URL);

  return flatpickrLoadingPromise;
}

function initDatePickers() {
  const dateInputs = document.querySelectorAll(".date-picker");
  const dateTimeInputs = document.querySelectorAll(".datetime-picker");

  if (dateInputs.length === 0 && dateTimeInputs.length === 0) {
    return;
  }

  ensureFlatpickrLoaded()
    .then(() => {
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
    .catch(() => {
      console.error("Không thể tải Flatpickr.");
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

document.body.addEventListener("htmx:responseError", function () {
  removePageLoading();
  alert("Có lỗi xảy ra khi xử lý yêu cầu. Vui lòng thử lại.");
});

document.body.addEventListener("htmx:sendError", function () {
  removePageLoading();
  alert("Không thể gửi yêu cầu đến máy chủ. Vui lòng kiểm tra lại kết nối.");
});
