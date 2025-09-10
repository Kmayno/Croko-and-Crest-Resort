
    const token = localStorage.getItem("token");
    const nome = localStorage.getItem("adminNome");

    if (!token) {
    window.location.href = "AdminLogin.html";
}

    document.getElementById("welcome").textContent = `Ciao ${nome}, benvenuto!`;

    // Logout
    document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.removeItem("token");
    localStorage.removeItem("adminNome");
    window.location.href = "AdminLogin.html";
});

    // --- MODAL ELIMINA ---
    let adminToDelete = null;

    function openModal(admin, card) {
    adminToDelete = {...admin, card};
    document.getElementById("modalText").textContent = `Sei sicuro di voler eliminare ${admin.nome}?`;
    document.getElementById("deleteModal").classList.remove("hidden");
}

    function closeModal() {
    adminToDelete = null;
    document.getElementById("deleteModal").classList.add("hidden");
}

    document.getElementById("cancelDelete").addEventListener("click", closeModal);
    document.getElementById("closeModal").addEventListener("click", closeModal);

    document.getElementById("confirmDelete").addEventListener("click", () => {
    if (!adminToDelete) return;

    fetch(`http://localhost:8080/api/admin-login/delete/${adminToDelete.idAdmin}`, {
    method: "DELETE",
    headers: {
    "Authorization": `Bearer ${token}`,
    "Content-Type": "application/json"
}
})
    .then(response => {
    if (!response.ok) throw new Error("Errore durante l'eliminazione");
    adminToDelete.card.remove();
    closeModal();
})
    .catch(err => {
    alert(err.message);
    closeModal();
});
});

    // --- MODAL MODIFICA ---
    let adminToEdit = null;

    function openEditModal(admin, card) {
    adminToEdit = {...admin, card};
    document.getElementById("editId").value = admin.idAdmin;
    document.getElementById("editNome").value = admin.nome;
    document.getElementById("editEmail").value = admin.email;
    document.getElementById("editPassword").value = ""; // vuoto di default
    document.getElementById("editModal").classList.remove("hidden");
}

    function closeEditModal() {
    adminToEdit = null;
    document.getElementById("editModal").classList.add("hidden");
}

    document.getElementById("cancelEdit").addEventListener("click", closeEditModal);
    document.getElementById("closeEditModal").addEventListener("click", closeEditModal);

    document.getElementById("editAdminForm").addEventListener("submit", (e) => {
    e.preventDefault();
    if (!adminToEdit) return;

    const updatedAdmin = {
    idAdmin: document.getElementById("editId").value,
    nome: document.getElementById("editNome").value,
    email: document.getElementById("editEmail").value,
    passwordAdmin: document.getElementById("editPassword").value
};

    fetch("http://localhost:8080/api/admin-login/update", {
    method: "PUT",
    headers: {
    "Authorization": `Bearer ${token}`,
    "Content-Type": "application/json"
},
    body: JSON.stringify(updatedAdmin)
})
    .then(response => {
    if (!response.ok) throw new Error("Errore durante la modifica");
    return response.json();
})
    .then(() => {
    loadAdmins(); // ricarico lista aggiornata
    closeEditModal();
})
    .catch(err => {
    alert(err.message);
    closeEditModal();
});
});

    // --- MODAL ELIMINA PRENOTAZIONE ---
    let prenotazioneToDelete = null;

    function openPrenModal(prenotazione, card) {
    prenotazioneToDelete = {...prenotazione, card};
    document.getElementById("prenModalText").textContent =
    `Sei sicuro di voler eliminare la prenotazione #${prenotazione.idPrenotazione}?`;
    document.getElementById("deletePrenModal").classList.remove("hidden");
}

    function closePrenModal() {
    prenotazioneToDelete = null;
    document.getElementById("deletePrenModal").classList.add("hidden");
}

    document.getElementById("cancelPrenDelete").addEventListener("click", closePrenModal);
    document.getElementById("closePrenModal").addEventListener("click", closePrenModal);

    document.getElementById("confirmPrenDelete").addEventListener("click", () => {
    if (!prenotazioneToDelete) return;

    fetch(`http://localhost:8080/api/prenotazioni/${prenotazioneToDelete.idPrenotazione}`, {
    method: "DELETE",
    headers: {
    "Authorization": `Bearer ${token}`,
    "Content-Type": "application/json"
}
})
    .then(response => {
    if (!response.ok) {
    return response.text().then(msg => {
    throw new Error(msg);
});
}
    loadPrenotazioni(); // aggiorna lista
    closePrenModal();
})
    .catch(err => {
    alert("Errore: " + err.message);
    closePrenModal();
});
});

    // --- CARICAMENTO ADMIN ---
    function loadAdmins() {
    fetch("http://localhost:8080/api/admin-login/getAllAdmin", {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        }
    })
        .then(response => {
            if (!response.ok) throw new Error("Errore nel caricamento admin");
            return response.json();
        })
        .then(admins => {
            const resultDiv = document.getElementById("result");
            resultDiv.innerHTML = "";

            admins.forEach(admin => {
                const card = document.createElement("div");
                card.className = "bg-white shadow rounded-2xl p-6 hover:shadow-lg transition relative";

                card.innerHTML = `
                        <h3 class="text-lg font-bold text-gray-800 mb-2">Admin: ${admin.nome}</h3>
                        <p class="text-gray-600 mb-4"><span class="font-medium">Email:</span> ${admin.email}</p>
                        <div class="flex space-x-2">
                            <button class="editBtn bg-green-900 hover:bg-blue-600 text-white px-3 py-1 rounded-lg">Modifica</button>
                            <button class="deleteBtn bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded-lg">Elimina</button>
                        </div>
                    `;

                card.querySelector(".deleteBtn").addEventListener("click", () => {
                    openModal(admin, card);
                });

                card.querySelector(".editBtn").addEventListener("click", () => {
                    openEditModal(admin, card);
                });

                resultDiv.appendChild(card);
            });
        })
        .catch(err => {
            document.getElementById("result").innerHTML = `
                    <div class="bg-red-100 text-red-700 p-4 rounded-lg col-span-full">
                        ${err.message}
                    </div>
                `;
        });
}

    // --- CARICAMENTO PRENOTAZIONI ---
    function loadPrenotazioni() {
    fetch("http://localhost:8080/api/prenotazioni", {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        }
    })
        .then(response => {
            if (!response.ok) throw new Error("Errore nel caricamento delle prenotazioni");
            return response.json();
        })
        .then(prenotazioni => {
            const prenotazioniDiv = document.getElementById("prenotazioniResult");
            prenotazioniDiv.innerHTML = "";

            prenotazioni.forEach(prenotazione => {
                const card = document.createElement("div");
                card.className = "bg-white shadow rounded-2xl p-6 hover:shadow-lg transition";

                card.innerHTML = `
                       <h3 class="text-lg font-bold text-gray-800 mb-2">Prenotazione #${prenotazione.idPrenotazione}</h3>
                       <p class="text-gray-600"><span class="font-medium">Nome cliente:</span> ${prenotazione.nomeCliente}</p>
                       <p class="text-gray-600"><span class="font-medium">Data Check-in:</span> ${prenotazione.dataInizio}</p>
                       <p class="text-gray-600"><span class="font-medium">Data Check-out:</span> ${prenotazione.dataFine}</p>
                       <p class="text-gray-600"><span class="font-medium">Num Stanza:</span> ${prenotazione.numeroStanza}</p>
                       <p class="text-gray-600"><span class="font-medium">Prezzo:</span> ${prenotazione.prezzoTotale}</p>
                        <div class="mt-4 flex justify-end">
                            <button class="deletePrenBtn bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded-lg">Elimina</button>
                        </div>
                    `;

                card.querySelector(".deletePrenBtn").addEventListener("click", () => {
                    openPrenModal(prenotazione, card);
                });

                prenotazioniDiv.appendChild(card);
            });
        })
        .catch(err => {
            document.getElementById("prenotazioniResult").innerHTML = `
                    <div class="bg-red-100 text-red-700 p-4 rounded-lg col-span-full">
                        ${err.message}
                    </div>
                `;
        });
}

    window.onload = () => {
    loadAdmins();
    loadPrenotazioni();
};

