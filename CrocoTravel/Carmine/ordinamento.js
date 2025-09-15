document.addEventListener("DOMContentLoaded", function () {

  // Prendiamo il menu a tendina
  let menu = document.getElementById("sortSelect");

  // Prendiamo il contenitore delle card (il div con classi row g-4)
  let contenitore = document.querySelector(".row.g-4");

  // Quando l'utente cambia selezione
  menu.addEventListener("change", function () {

    // Leggiamo il tipo di ordinamento selezionato (es: "prezzo")
    let tipo = menu.value;

    // Se non ha scelto nulla, non facciamo nulla
    if (tipo === "Seleziona") {
      return;
    }

    // Prendiamo tutte le colonne (che contengono le card)
    let colonne = contenitore.children;

    // Trasformiamo la collezione HTML in un vero array
    let lista = [];

    for (let i = 0; i < colonne.length; i++) {
      lista.push(colonne[i]);
    }

    // Ordiniamo l'array in base al valore del data- (es: data-prezzo)
    lista.sort(function (a, b) {
      let valoreA = parseFloat(a.querySelector(".card").dataset[tipo]);
      let valoreB = parseFloat(b.querySelector(".card").dataset[tipo]);

      // Ordine crescente
      return valoreA - valoreB;
    });

    // Svuotiamo il contenitore
    contenitore.innerHTML = "";

    // Reinseriamo le card ordinate
    for (let i = 0; i < lista.length; i++) {
      contenitore.appendChild(lista[i]);
    }

  });

});