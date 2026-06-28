if ("scrollRestoration" in history) {
  history.scrollRestoration = "manual";
}

function getMainContent() {
  return document.getElementById("main-content");
}

function scrollMainToTop() {
  const mainContent = getMainContent();

  if (mainContent) {
    mainContent.scrollTop = 0;
  }
}

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
          const toggle = dropdown.querySelector(".app-nav-link");

          if (toggle) {
            toggle.classList.add("active");
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

function initAppUI() {
  setActiveNavbar();
}

document.addEventListener("DOMContentLoaded", function () {
  initAppUI();
  scrollMainToTop();
});

document.body.addEventListener("htmx:beforeRequest", function () {
  closeNavbarMenu();
});

document.body.addEventListener("htmx:beforeSwap", function (event) {
  if (event.detail.target && event.detail.target.id === "main-content") {
    event.detail.target.classList.add("is-page-loading");
    scrollMainToTop();
  }
});

document.body.addEventListener("htmx:afterSwap", function (event) {
  initAppUI();

  if (event.detail.target && event.detail.target.id === "main-content") {
    scrollMainToTop();
  }
});

document.body.addEventListener("htmx:afterSettle", function (event) {
  if (event.detail.target && event.detail.target.id === "main-content") {
    scrollMainToTop();
    event.detail.target.classList.remove("is-page-loading");
  }
});
