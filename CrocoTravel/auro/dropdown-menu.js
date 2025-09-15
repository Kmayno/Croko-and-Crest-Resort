// Gestione dello stato attivo nel dropdown
document.addEventListener("DOMContentLoaded", function () {
  const dropdownItems = document.querySelectorAll(".dropdown-item-nav");
  const currentPage =
    window.location.pathname.split("/").pop() || "homepage.html";

  // Rimuovi tutti gli stati attivi
  dropdownItems.forEach((item) => {
    item.classList.remove("active");
  });

  // Imposta lo stato attivo per la pagina corrente
  dropdownItems.forEach((item) => {
    const href = item.getAttribute("href");
    if (
      href &&
      (href.includes(currentPage) ||
        (currentPage === "homepage.html" && href === "homepage.html"))
    ) {
      item.classList.add("active");
    }
  });

  // Gestione del click sui link del dropdown
  dropdownItems.forEach((item) => {
    item.addEventListener("click", function (e) {
      // Rimuovi stato attivo da tutti gli elementi
      dropdownItems.forEach((link) => link.classList.remove("active"));

      // Aggiungi stato attivo all'elemento cliccato
      this.classList.add("active");

      // Chiudi il dropdown dopo il click
      const dropdown = document.querySelector(".dropdown-menu-custom");
      if (dropdown && dropdown.classList.contains("show")) {
        // Usa Bootstrap per chiudere il dropdown
        const bsDropdown = bootstrap.Dropdown.getInstance(
          document.querySelector(".navbar-toggler-dropdown")
        );
        if (bsDropdown) {
          bsDropdown.hide();
        }
      }
    });
  });

  // Migliora l'accessibilità con la navigazione da tastiera
  document.addEventListener("keydown", function (e) {
    const dropdown = document.querySelector(".dropdown-menu-custom");
    const isDropdownOpen = dropdown && dropdown.classList.contains("show");

    if (isDropdownOpen && e.key === "Escape") {
      const bsDropdown = bootstrap.Dropdown.getInstance(
        document.querySelector(".navbar-toggler-dropdown")
      );
      if (bsDropdown) {
        bsDropdown.hide();
      }
      document.querySelector(".navbar-toggler-dropdown").focus();
    }
  });
});
