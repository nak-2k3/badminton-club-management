if ("scrollRestoration" in history) {
  history.scrollRestoration = "manual";
}

function getMainContent() {
  return document.getElementById("main-content");
}

function scrollToTop() {
  const mainContent = getMainContent();

  if (mainContent) {
    mainContent.scrollTop = 0;
  }

  window.scrollTo(0, 0);
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

function updateAttendanceBadge(form) {
  const row = form.closest("tr");
  if (!row) return;

  const statusInput = form.querySelector("input[name='attendanceStatus']");
  if (!statusInput) return;

  const status = statusInput.value;
  const badge = row.querySelector(".attendance-badge");
  if (!badge) return;

  badge.classList.remove("bg-success", "bg-danger", "bg-secondary");

  if (status === "PRESENT") {
    badge.classList.add("bg-success");
    badge.textContent = "Có mặt";
    return;
  }

  if (status === "ABSENT") {
    badge.classList.add("bg-danger");
    badge.textContent = "Vắng";
    return;
  }

  badge.classList.add("bg-secondary");
  badge.textContent = "Chưa điểm danh";
}

async function submitAttendanceForm(form) {
  const submitButton = form.querySelector("button[type='submit']");
  const originalText = submitButton ? submitButton.textContent : "";

  try {
    if (submitButton) {
      submitButton.disabled = true;
      submitButton.textContent = "...";
    }

    const response = await fetch(form.action, {
      method: "POST",
      body: new FormData(form),
      credentials: "same-origin",
      redirect: "follow",
      headers: {
        "X-Requested-With": "fetch",
      },
    });

    if (!response.ok) {
      alert("Không thể cập nhật điểm danh. Vui lòng thử lại.");
      return;
    }

    updateAttendanceBadge(form);
  } catch (error) {
    alert("Có lỗi xảy ra khi cập nhật điểm danh.");
  } finally {
    if (submitButton) {
      submitButton.disabled = false;
      submitButton.textContent = originalText;
    }
  }
}

function initAttendanceForms() {
  document.querySelectorAll(".attendance-form").forEach((form) => {
    if (form.dataset.attendanceBound === "true") return;

    form.dataset.attendanceBound = "true";

    form.addEventListener("submit", function (event) {
      event.preventDefault();
      submitAttendanceForm(form);
    });
  });
}

function initAppUI() {
  setActiveNavbar();
  initAttendanceForms();
}

document.addEventListener("DOMContentLoaded", function () {
  initAppUI();
});

document.body.addEventListener("htmx:beforeRequest", function () {
  closeNavbarMenu();
});

document.body.addEventListener("htmx:afterSwap", function (event) {
  initAppUI();

  if (event.detail.target && event.detail.target.id === "main-content") {
    scrollToTop();
  }
});

document.body.addEventListener("htmx:afterSettle", function (event) {
  if (event.detail.target && event.detail.target.id === "main-content") {
    scrollToTop();
  }
});
