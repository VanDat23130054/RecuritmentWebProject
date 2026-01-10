/**
 * JobHunter - Main JavaScript
 * Handles responsive navigation, mobile menu, and sidebar toggles
 */

document.addEventListener("DOMContentLoaded", function () {
  // Initialize all interactive components
  initMobileMenu();
  initUserDropdown();
  initSidebarToggle();
  initSidebarOverlay();
});

/**
 * Mobile Menu Toggle (Hamburger Menu)
 * Controls the main navigation visibility on mobile devices
 */
function initMobileMenu() {
  const mobileMenuToggle = document.querySelector(".mobile-menu-toggle");
  const mainNav = document.querySelector(".main-nav");
  const headerActions = document.querySelector(".header-actions");

  if (!mobileMenuToggle) return;

  mobileMenuToggle.addEventListener("click", function (e) {
    e.preventDefault();
    e.stopPropagation();

    const isOpen = this.classList.contains("active");

    if (isOpen) {
      // Close the menu
      closeMobileMenu();
    } else {
      // Open the menu
      this.classList.add("active");

      if (mainNav) {
        mainNav.classList.add("mobile-open");
      }

      if (headerActions) {
        headerActions.classList.add("mobile-open");
      }

      // Change hamburger icon to X
      const icon = this.querySelector("i");
      if (icon) {
        icon.classList.remove("fa-bars");
        icon.classList.add("fa-times");
      }
    }
  });

  // Close mobile menu when clicking outside
  document.addEventListener("click", function (e) {
    const isClickInsideToggle = mobileMenuToggle.contains(e.target);
    const isClickInsideNav = mainNav?.contains(e.target);
    const isClickInsideActions = headerActions?.contains(e.target);

    if (!isClickInsideToggle && !isClickInsideNav && !isClickInsideActions) {
      closeMobileMenu();
    }
  });

  // Close mobile menu on window resize to desktop
  window.addEventListener("resize", function () {
    if (window.innerWidth > 768) {
      closeMobileMenu();
    }
  });
}

function closeMobileMenu() {
  const mobileMenuToggle = document.querySelector(".mobile-menu-toggle");
  const mainNav = document.querySelector(".main-nav");
  const headerActions = document.querySelector(".header-actions");

  if (mobileMenuToggle) {
    mobileMenuToggle.classList.remove("active");
    const icon = mobileMenuToggle.querySelector("i");
    if (icon) {
      icon.classList.remove("fa-times");
      icon.classList.add("fa-bars");
    }
  }

  if (mainNav) {
    mainNav.classList.remove("mobile-open");
  }

  if (headerActions) {
    headerActions.classList.remove("mobile-open");
  }
}

/**
 * User Dropdown Menu
 * Controls the user profile dropdown in the header
 */
function initUserDropdown() {
  const userProfileBtn = document.querySelector(".user-profile-btn");
  const userDropdown = document.querySelector(".user-dropdown");

  if (!userProfileBtn || !userDropdown) return;

  userProfileBtn.addEventListener("click", function (e) {
    e.stopPropagation();
    userDropdown.classList.toggle("show");
    this.classList.toggle("active");
  });

  // Close dropdown when clicking outside
  document.addEventListener("click", function (e) {
    if (
      !userProfileBtn.contains(e.target) &&
      !userDropdown.contains(e.target)
    ) {
      userDropdown.classList.remove("show");
      userProfileBtn.classList.remove("active");
    }
  });
}

/**
 * Dashboard Sidebar Toggle
 * Controls the sidebar visibility on tablet/mobile devices
 */
function initSidebarToggle() {
  const sidebarToggle = document.querySelector(".sidebar-toggle");
  const sidebar = document.querySelector(".dashboard-sidebar");
  const overlay = document.querySelector(".sidebar-overlay");

  if (!sidebarToggle || !sidebar) return;

  sidebarToggle.addEventListener("click", function (e) {
    e.stopPropagation();
    toggleSidebar();
  });

  // Close sidebar when clicking overlay
  if (overlay) {
    overlay.addEventListener("click", function () {
      closeSidebar();
    });
  }

  // Close sidebar on window resize to desktop
  window.addEventListener("resize", function () {
    if (window.innerWidth > 1024) {
      closeSidebar();
    }
  });

  // Close sidebar when clicking a nav link (on mobile)
  const navLinks = sidebar.querySelectorAll(".sidebar-nav a");
  navLinks.forEach((link) => {
    link.addEventListener("click", function () {
      if (window.innerWidth <= 1024) {
        closeSidebar();
      }
    });
  });
}

function toggleSidebar() {
  const sidebar = document.querySelector(".dashboard-sidebar");
  const overlay = document.querySelector(".sidebar-overlay");
  const sidebarToggle = document.querySelector(".sidebar-toggle");

  if (sidebar) {
    sidebar.classList.toggle("open");
  }

  if (overlay) {
    overlay.classList.toggle("show");
  }

  if (sidebarToggle) {
    const icon = sidebarToggle.querySelector("i");
    if (icon) {
      if (sidebar?.classList.contains("open")) {
        icon.classList.remove("fa-bars");
        icon.classList.add("fa-times");
      } else {
        icon.classList.remove("fa-times");
        icon.classList.add("fa-bars");
      }
    }
  }

  // Prevent body scroll when sidebar is open
  document.body.classList.toggle(
    "sidebar-open",
    sidebar?.classList.contains("open")
  );
}

function closeSidebar() {
  const sidebar = document.querySelector(".dashboard-sidebar");
  const overlay = document.querySelector(".sidebar-overlay");
  const sidebarToggle = document.querySelector(".sidebar-toggle");

  if (sidebar) {
    sidebar.classList.remove("open");
  }

  if (overlay) {
    overlay.classList.remove("show");
  }

  if (sidebarToggle) {
    const icon = sidebarToggle.querySelector("i");
    if (icon) {
      icon.classList.remove("fa-times");
      icon.classList.add("fa-bars");
    }
  }

  document.body.classList.remove("sidebar-open");
}

/**
 * Initialize sidebar overlay element
 * Creates overlay if it doesn't exist in dashboard pages
 */
function initSidebarOverlay() {
  const sidebar = document.querySelector(".dashboard-sidebar");

  if (!sidebar) return;

  // Check if overlay already exists
  let overlay = document.querySelector(".sidebar-overlay");

  if (!overlay) {
    // Create overlay element
    overlay = document.createElement("div");
    overlay.className = "sidebar-overlay";
    document.body.appendChild(overlay);

    // Add click handler to close sidebar
    overlay.addEventListener("click", function () {
      closeSidebar();
    });
  }
}

/**
 * Utility function to check if we're on mobile
 */
function isMobile() {
  return window.innerWidth <= 768;
}

/**
 * Utility function to check if we're on tablet
 */
function isTablet() {
  return window.innerWidth <= 1024 && window.innerWidth > 768;
}

/**
 * Utility function to check if we're on mobile or tablet
 */
function isMobileOrTablet() {
  return window.innerWidth <= 1024;
}
