
/*
  Dashboard single-file
  - Uso: salva come .html e apri nel browser.
  - Se non hai token la pagina non reindirizza: mostra banner con pulsante "Simula login" (utile per debug).
  - Controlla console per dettagli (CORS/401).
*/
document.addEventListener('DOMContentLoaded', () => {
    console.log('Dashboard caricata');

    const token = localStorage.getItem("token");
    const nome = localStorage.getItem("adminNome");
    const topBanner = document.getElementById('topBanner');
    const welcomeEl = document.getElementById('welcome');
    welcomeEl.textContent = `Admin loggato: ${nome || 'Admin'}`;

    // Sezioni
    const sections = {
        dashboard: document.getElementById('section-dashboard'),
        admins: document.getElementById('section-admins'),
        prenotazioni: document.getElementById('section-prenotazioni'),
        camere: document.getElementById('section-camere'),
        aggiunte: document.getElementById('section-aggiunte')
    };

    // Sidebar menu
    document.querySelectorAll('.menuBtn').forEach(btn => {
        btn.addEventListener('click', () => showSection(btn.getAttribute('data-section')));
    });

    function showSection(key) {
        Object.values(sections).forEach(s => s.classList.add('hidden'));
        if (sections[key]) sections[key].classList.remove('hidden');
        if (key === 'admins') loadAdmins();
        if (key === 'prenotazioni') loadPrenotazioni();
        if (key === 'camere') loadCamere();
        if (key === 'aggiunte') loadOpzioni();
    }

    // Default
    showSection('admins');

    // Logout
    document.getElementById('logoutBtn').addEventListener('click', () => {
        localStorage.removeItem('token');
        localStorage.removeItem('adminNome');
        window.location.href = 'AdminLogin.html';
    });

    // ===== ADMIN LOGIC =====
    let adminToEdit = null;
    let adminToDelete = null;

    document.getElementById('refreshAdmins').addEventListener('click', loadAdmins);

    function loadAdmins() {
        const resultDiv = document.getElementById('result');
        resultDiv.innerHTML = `<div class="col-span-full text-gray-500">Caricamento...</div>`;
        const tokenLocal = localStorage.getItem('token');

        if (!tokenLocal) return renderAdmins(sampleAdmins());

        fetch('http://localhost:8080/api/admin-login/getAllAdmin', {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${tokenLocal}`, 'Content-Type': 'application/json' }
        })
            .then(res => res.ok ? res.json() : Promise.reject('Errore caricamento admins'))
            .then(admins => renderAdmins(admins))
            .catch(err => {
                console.error(err);
                renderAdmins(sampleAdmins());
                showBanner('Errore caricamento admins: ' + err, 'red');
            });
    }

    function renderAdmins(admins) {
        const resultDiv = document.getElementById('result');
        resultDiv.innerHTML = '';
        admins.forEach(admin => {
            const card = document.createElement('div');
            card.className = 'bg-white shadow rounded-2xl p-6 hover:shadow-lg transition relative';
            card.innerHTML = `
        <h3 class="text-lg font-bold text-gray-800 mb-2">Admin: ${escapeHtml(admin.nome)}</h3>
        <p class="text-gray-600 mb-4"><span class="font-medium">Email:</span> ${escapeHtml(admin.email)}</p>
        <div class="flex space-x-2">
          <button class="editBtn bg-green-700 hover:bg-green-600 text-white px-3 py-1 rounded-lg">Modifica</button>
          <button class="deleteBtn bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded-lg">Elimina</button>
        </div>
      `;
            card.querySelector('.editBtn').addEventListener('click', () => openEditAdmin(admin, card));
            card.querySelector('.deleteBtn').addEventListener('click', () => openDeleteAdmin(admin, card));
            resultDiv.appendChild(card);
        });
    }

    function openEditAdmin(admin, card) {
        adminToEdit = { ...admin, card };
        document.getElementById('editId').value = admin.idAdmin;
        document.getElementById('editNome').value = admin.nome;
        document.getElementById('editEmail').value = admin.email;
        document.getElementById('editPassword').value = '';
        document.getElementById('editModal').classList.remove('hidden');
    }

    function closeEditAdmin() {
        adminToEdit = null;
        document.getElementById('editModal').classList.add('hidden');
    }

    document.getElementById('cancelEdit').addEventListener('click', closeEditAdmin);
    document.getElementById('closeEditModal').addEventListener('click', closeEditAdmin);

    document.getElementById('editAdminForm').addEventListener('submit', e => {
        e.preventDefault();
        if (!adminToEdit) return;
        const updated = {
            idAdmin: document.getElementById('editId').value,
            nome: document.getElementById('editNome').value,
            email: document.getElementById('editEmail').value,
            passwordAdmin: document.getElementById('editPassword').value
        };
        fetch('http://localhost:8080/api/admin-login/update', {
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
            body: JSON.stringify(updated)
        })
            .then(res => res.ok ? res.json() : Promise.reject('Errore modifica admin'))
            .then(() => {
                closeEditAdmin();
                loadAdmins();
                showBanner('Admin aggiornato', 'green');
            })
            .catch(err => showBanner(err, 'red'));
    });

    function openDeleteAdmin(admin, card) {
        adminToDelete = { ...admin, card };
        document.getElementById('modalText').textContent = `Sei sicuro di voler eliminare ${admin.nome}?`;
        document.getElementById('deleteModal').classList.remove('hidden');
    }

    function closeDeleteAdmin() {
        adminToDelete = null;
        document.getElementById('deleteModal').classList.add('hidden');
    }

    document.getElementById('cancelDelete').addEventListener('click', closeDeleteAdmin);
    document.getElementById('closeModal').addEventListener('click', closeDeleteAdmin);

    document.getElementById('confirmDelete').addEventListener('click', () => {
        if (!adminToDelete) return;
        fetch(`http://localhost:8080/api/admin-login/delete/${adminToDelete.idAdmin}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
        })
            .then(res => res.ok ? res : Promise.reject('Errore eliminazione admin'))
            .then(() => {
                adminToDelete.card.remove();
                closeDeleteAdmin();
                showBanner('Admin eliminato', 'green');
            })
            .catch(err => showBanner(err, 'red'));
    });

    // ===== PRENOTAZIONI =====
    let prenToDelete = null;
    document.getElementById('refreshPren').addEventListener('click', loadPrenotazioni);

    function loadPrenotazioni() {
        const prenDiv = document.getElementById('prenotazioniResult');
        prenDiv.innerHTML = `<div class="col-span-full text-gray-500">Caricamento...</div>`;
        fetch('http://localhost:8080/api/prenotazioni', {
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
        })
            .then(res => res.ok ? res.json() : Promise.reject('Errore caricamento prenotazioni'))
            .then(pren => renderPrenotazioni(pren))
            .catch(err => {
                console.error(err);
                renderPrenotazioni(samplePrenotazioni());
                showBanner('Errore caricamento prenotazioni', 'red');
            });
    }

    function renderPrenotazioni(prenotazioni) {
        const prenDiv = document.getElementById('prenotazioniResult');
        prenDiv.innerHTML = '';
        prenotazioni.forEach(pr => {
            const card = document.createElement('div');
            card.className = 'bg-white shadow rounded-2xl p-6 hover:shadow-lg transition';
            card.innerHTML = `
        <h3 class="text-lg font-bold text-gray-800 mb-2">Prenotazione #${pr.idPrenotazione}</h3>
        <p class="text-gray-600"><span class="font-medium">Nome cliente:</span> ${pr.nomeCliente}</p>
        <p class="text-gray-600"><span class="font-medium">Check-in:</span> ${pr.dataInizio}</p>
        <p class="text-gray-600"><span class="font-medium">Check-out:</span> ${pr.dataFine}</p>
        <p class="text-gray-600"><span class="font-medium">Stanza:</span> ${pr.numeroStanza}</p>
        <p class="text-gray-600"><span class="font-medium">Prezzo:</span> ${pr.prezzoTotale}</p>
        <p class="text-gray-600 flex items-center space-x-2">
            <span class="font-medium">Stato:</span> 
                <input 
                    type="checkbox" 
                    class="togglePrenotazione" 
                    data-id="${pr.idPrenotazione}" 
                    ${pr.statoPrenotazione ? "checked" : ""}>
            <span>${pr.statoPrenotazione ? "Confermata" : "Non confermata"}</span>
        </p>

        <div class="mt-4 flex justify-end">
          <button class="deletePrenBtn bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded-lg">Elimina</button>
        </div>
      `;
            card.querySelector('.deletePrenBtn').addEventListener('click', () => openDeletePren(pr, card));
            prenDiv.appendChild(card);
        });
        document.querySelectorAll(".togglePrenotazione").forEach(chk => {
            chk.addEventListener("change", async (e) => {
                const id = e.target.dataset.id;
                const nuovoStato = e.target.checked; // true/false

                try {
                    const res = await fetch(`http://localhost:8080/api/prenotazioni/${id}?statoConfermato=${nuovoStato}`, {
                        method: "PUT",
                        headers: { "Authorization": `Bearer ${token}` }
                    });

                    if (!res.ok) throw new Error("Errore aggiornamento stato");

                    showBanner(
                        `Prenotazione #${id} aggiornata a ${nuovoStato ? "Confermata" : "Non confermata"}`,
                        "green"
                    );
                    loadPrenotazioni(); // ricarica la lista aggiornata
                } catch (err) {
                    showBanner("Errore: " + err.message, "red");
                    e.target.checked = !nuovoStato; // rollback se fallisce
                }
            });
        });
    }

    function openDeletePren(pren, card) {
        prenToDelete = { ...pren, card };
        document.getElementById('prenModalText').textContent = `Sei sicuro di voler eliminare la prenotazione #${pren.idPrenotazione}?`;
        document.getElementById('deletePrenModal').classList.remove('hidden');
    }

    function closeDeletePren() {
        prenToDelete = null;
        document.getElementById('deletePrenModal').classList.add('hidden');
    }

    document.getElementById('cancelPrenDelete').addEventListener('click', closeDeletePren);
    document.getElementById('closePrenModal').addEventListener('click', closeDeletePren);

    document.getElementById('confirmPrenDelete').addEventListener('click', () => {
        if (!prenToDelete) return;
        fetch(`http://localhost:8080/api/prenotazioni/${prenToDelete.idPrenotazione}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
        })
            .then(res => res.ok ? res : Promise.reject('Errore eliminazione prenotazione'))
            .then(() => {
                prenToDelete.card.remove();
                closeDeletePren();
                showBanner('Prenotazione eliminata', 'green');
            })
            .catch(err => showBanner(err, 'red'));
    });

    // ===== CAMERE =====
    let cameraToEdit = null;

    document.getElementById('refreshCamere').addEventListener('click', loadCamere);

    function loadCamere() {
        const camereDiv = document.getElementById('camereResult');
        camereDiv.innerHTML = `<div class="col-span-full text-gray-500">Caricamento...</div>`;
        fetch('http://localhost:8080/api/camere/getAllRooms', {
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
        })
            .then(res => res.ok ? res.json() : Promise.reject('Errore caricamento camere'))
            .then(camere => renderCamere(camere))
            .catch(err => {
                console.error(err);
                renderCamere(sampleCamere());
                showBanner('Errore caricamento camere', 'red');
            });
    }

    function renderCamere(camere) {
        const camereDiv = document.getElementById('camereResult');
        camereDiv.innerHTML = '';

        camere.forEach(c => {
            const card = document.createElement('div');
            card.className = 'bg-white shadow rounded-2xl p-6 hover:shadow-lg transition';
            card.innerHTML = `
            <h3 class="text-lg font-bold text-gray-800 mb-2">Camera #${c.numeroStanza}</h3>
            <p class="text-gray-600"><span class="font-medium">Tipo:</span> ${c.tipoCamera || c.tipo}</p>
            <p class="text-gray-600"><span class="font-medium">Descrizione:</span> ${c.descrizione || ''}</p>
            <p class="text-gray-600"><span class="font-medium">Prezzo:</span> €${c.prezzoBaseNotte || c.prezzo || 0}</p>
            <p class="text-gray-600"><span class="font-medium">Disponibile:</span> ${c.disponibile ? 'si' : 'no'}</p>
            <div class="mt-4 flex justify-end space-x-2">
                <button class="editCameraBtn bg-green-700 hover:bg-green-600 text-white px-3 py-1 rounded-lg">Modifica</button>
                <button class="deleteCameraBtn bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded-lg">Elimina</button>
            </div>
        `;

            // Pulsante Modifica → apre modal
            card.querySelector('.editCameraBtn').addEventListener('click', () => openEditCamera(c));
            card.querySelector('.deleteCameraBtn').addEventListener('click', () => {
                openDeleteCamera(c, card);
            });
            // Pulsante Elimina → conferma ed elimina
            /*  card.querySelector('.deleteCameraBtn').addEventListener('click', () => {
                  if (!confirm(`Vuoi eliminare la camera #${c.numeroStanza}?`)) return;
                  const token = localStorage.getItem('token');
                  fetch(`http://localhost:8080/api/camere/${c.idCamera}`, {
                      method: 'DELETE',
                      headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
                  })
                      .then(res => res.ok ? res : Promise.reject('Errore eliminazione camera'))
                      .then(() => {
                          showBanner('Camera eliminata', 'green');
                          loadCamere(); // ricarica la lista
                      })
                      .catch(err => showBanner(err, 'red'));
              });*/

            camereDiv.appendChild(card);
        });
    }




    function openEditCamera(cam) {
        cameraToEdit = { ...cam };
        document.getElementById('editCameraId').value = cam.idCamera;
        document.getElementById('editNumeroStanza').value = cam.numeroStanza;
        document.getElementById('editTipoCamera').value = cam.tipo || cam.tipoCamera || '';
        document.getElementById('editDescrizione').value = cam.descrizione || '';
        document.getElementById('editPrezzo').value = cam.prezzoBaseNotte || cam.prezzo || 0;
        document.getElementById('editDisponibile').checked = cam.disponibile;
        document.getElementById('editCameraModal').classList.remove('hidden');
    }

    function closeEditCamera() {
        cameraToEdit = null;
        document.getElementById('editCameraModal').classList.add('hidden');
    }

    document.getElementById('cancelEditCamera').addEventListener('click', closeEditCamera);
    document.getElementById('closeEditCameraModal').addEventListener('click', closeEditCamera);

    document.getElementById('editCameraForm').addEventListener('submit', (e) => {
        e.preventDefault();
        if (!cameraToEdit) return;

        const token = localStorage.getItem('token');
        const updated = {
            numeroStanza: Number(document.getElementById('editNumeroStanza').value),
            tipoCamera: document.getElementById('editTipoCamera').value,
            descrizione: document.getElementById('editDescrizione').value,
            prezzoBaseNotte: Number(document.getElementById('editPrezzo').value),
            disponibile: document.getElementById('editDisponibile').checked
        };

        fetch(`http://localhost:8080/api/camere/${cameraToEdit.idCamera}`, {
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
            body: JSON.stringify(updated)
        })
            .then(res => res.ok ? res.text() : Promise.reject('Errore modifica camera'))
            .then(msg => {
                closeEditCamera();
                loadCamere(); // ricarica la lista delle camere
                showBanner(msg ||'Camera aggiornata', 'green');
            })
            .catch(err => showBanner(err, 'red'));
    });

    let cameraToDelete = null;

// Apri il modal di conferma
    function openDeleteCamera(camera, card) {
        cameraToDelete = { ...camera, card };
        document.getElementById('cameraModalText').textContent =
            `Sei sicuro di voler eliminare la camera #${camera.numeroStanza}?`;
        document.getElementById('deleteCameraModal').classList.remove('hidden');
    }

// Chiudi il modal
    function closeDeleteCamera() {
        cameraToDelete = null;
        document.getElementById('deleteCameraModal').classList.add('hidden');
    }

// Eventi dei bottoni del modal
    document.getElementById('cancelCameraDelete').addEventListener('click', closeDeleteCamera);
    document.getElementById('closeCameraModal').addEventListener('click', closeDeleteCamera);

// Conferma eliminazione
    document.getElementById('confirmCameraDelete').addEventListener('click', async () => {
        if (!cameraToDelete) return;
        const token = localStorage.getItem('token');

        try {
            const res = await fetch(`http://localhost:8080/api/camere/${cameraToDelete.idCamera}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
            });

            if (!res.ok) {
                const txt = await res.text();
                throw new Error(txt || `Errore server (${res.status})`);
            }

            // Rimuovi la card dalla UI
            cameraToDelete.card.remove();
            closeDeleteCamera();
            showBanner(`Camera #${cameraToDelete.numeroStanza} eliminata con successo`, 'green');

        } catch (err) {
            showBanner('Errore eliminazione: ' + err.message, 'red');
        }
    });








    // --- MODAL AGGIUNGI CAMERA ---
    const addCameraModal = document.getElementById("addCameraModal");
    const addCameraBtn = document.getElementById("addCameraBtn");
    const cancelAddCamera = document.getElementById("cancelAddCamera");
    const closeAddCameraModal = document.getElementById("closeAddCameraModal");

    function openAddCameraModal() {
        document.getElementById("addCameraForm").reset();
        addCameraModal.classList.remove("hidden");
    }

    function closeAddCameraModalFn() {
        addCameraModal.classList.add("hidden");
    }

    addCameraBtn.addEventListener("click", openAddCameraModal);
    cancelAddCamera.addEventListener("click", closeAddCameraModalFn);
    closeAddCameraModal.addEventListener("click", closeAddCameraModalFn);

    // Submit nuova camera
    document.getElementById("addCameraForm").addEventListener("submit", (e) => {
        e.preventDefault();

        const newCamera = {
            numeroStanza: document.getElementById("numeroStanza").value,
            tipoCamera: document.getElementById("tipoCamera").value,
            descrizione: document.getElementById("descrizione").value,
            prezzoBaseNotte: parseFloat(document.getElementById("prezzoBaseNotte").value),
            disponibile: document.getElementById("disponibile").checked
        };

        fetch("http://localhost:8080/api/camere/addRoom", {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(newCamera)
        })
            .then(res => {
                if (!res.ok) throw new Error("Errore durante inserimento camera");
                closeAddCameraModalFn();
                loadCamere(); // ricarica lista
                showBanner("Camera aggiunta con successo", "green");
            })
            .catch(err => showBanner(err.message, "red"));
    });



    /////// gestione senzione personalizzazioni
    const addOpzioneModal = document.getElementById("addOpzioneModal");
    const addOpzioneBtn = document.getElementById("addOpzioneBtn");
    const cancelAddOpzione = document.getElementById("cancelAddOpzione");
    const closeAddOpzioneModal = document.getElementById("closeAddOpzioneModal");

    function openAddOpzioneModal() {
        document.getElementById("addOpzioneForm").reset();
        addOpzioneModal.classList.remove("hidden");
    }

    function closeAddOpzioneModalFn() {
        addOpzioneModal.classList.add("hidden");
    }

    addOpzioneBtn.addEventListener("click", openAddOpzioneModal);
    cancelAddOpzione.addEventListener("click", closeAddOpzioneModalFn);
    closeAddOpzioneModal.addEventListener("click", closeAddOpzioneModalFn);


    document.getElementById("addOpzioneForm").addEventListener("submit", (e) => {
        e.preventDefault();

        const newOpzione = {
            nomeOpzione: document.getElementById("nomeOpzione").value,
            prezzoAggiuntivo: parseFloat(document.getElementById("prezzoAggiuntivo").value)
        };

        const token = localStorage.getItem("token");

        fetch("http://localhost:8080/api/options/addOption", {
            method: "POST",
            headers: { "Authorization": `Bearer ${token}`, "Content-Type": "application/json" },
            body: JSON.stringify(newOpzione)
        })
            .then(res => {
                if (!res.ok) throw new Error("Errore durante l'inserimento dell'opzione");
                closeAddOpzioneModalFn();
                loadOpzioni(); // ricarica lista opzioni
                showBanner("Opzione aggiunta con successo", "green");
            })
            .catch(err => showBanner(err.message, "red"));
    });
    //////////eliminare opzione
    let opzioneToDelete = null;

    // Mostra la sezione
    sections.aggiunte = document.getElementById('section-aggiunte');

    document.getElementById('refreshOpzioni').addEventListener('click', loadOpzioni);

    // Funzione per caricare le opzioni
    function loadOpzioni() {
        const resultDiv = document.getElementById('opzioniResult');
        resultDiv.innerHTML = `<div class="col-span-full text-gray-500">Caricamento...</div>`;
        const token = localStorage.getItem('token');

        fetch('http://localhost:8080/api/options/getAllOptions', {
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
        })
            .then(res => res.ok ? res.json() : Promise.reject('Errore caricamento opzioni'))
            .then(opzioni => renderOpzioni(opzioni))
            .catch(err => {
                console.error(err);
                showBanner('Errore caricamento opzioni: ' + err, 'red');
                resultDiv.innerHTML = '';
            });
    }

    // Renderizza le card delle opzioni
    function renderOpzioni(opzioni) {
        const resultDiv = document.getElementById('opzioniResult');
        resultDiv.innerHTML = '';

        opzioni.forEach(o => {
            const card = document.createElement('div');
            card.className = 'bg-white shadow rounded-2xl p-6 hover:shadow-lg transition';
            card.innerHTML = `
    <h3 class="text-lg font-bold text-gray-800 mb-2">${o.nomeOpzione}</h3>
    <p class="text-gray-600 mb-2"><span class="font-medium">Prezzo aggiuntivo:</span> €${o.prezzoAggiuntivo}</p>
    <div class="mt-4 flex justify-end space-x-2">
        <button class="editOpzioneBtn bg-green-700 hover:bg-green-600 text-white px-3 py-1 rounded-lg">Modifica</button>
        <button class="deleteOpzioneBtn bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded-lg">Elimina</button>
    </div>
`;

            card.querySelector('.deleteOpzioneBtn').addEventListener('click', () => openDeleteOpzione(o, card));
            card.querySelector('.editOpzioneBtn').addEventListener('click', () => openEditOpzione(o, card));
            resultDiv.appendChild(card);
        });
    }

    // Modal elimina opzione
    function openDeleteOpzione(opzione, card) {
        opzioneToDelete = { ...opzione, card };
        document.getElementById('opzioneModalText').textContent =
            `Sei sicuro di voler eliminare l'opzione "${opzione.nomeOpzione}"?`;
        document.getElementById('deleteOpzioneModal').classList.remove('hidden');
    }

    function closeDeleteOpzione() {
        opzioneToDelete = null;
        document.getElementById('deleteOpzioneModal').classList.add('hidden');
    }

    document.getElementById('cancelOpzioneDelete').addEventListener('click', closeDeleteOpzione);
    document.getElementById('closeOpzioneModal').addEventListener('click', closeDeleteOpzione);

    document.getElementById('confirmOpzioneDelete').addEventListener('click', async () => {
        if (!opzioneToDelete) return;
        const token = localStorage.getItem('token');
        const nome = opzioneToDelete.nomeOpzione;
        try {
            const res = await fetch(`http://localhost:8080/api/options/delete/${opzioneToDelete.idOpzione}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
            });

            if (!res.ok) throw new Error(`Errore server (${res.status})`);

            // rimuovi card dalla UI
            opzioneToDelete.card.remove();
            closeDeleteOpzione();
            showBanner(`Opzione "${nome}" eliminata con successo`, 'green');

        } catch (err) {
            showBanner('Errore eliminazione: ' + err.message, 'red');
        }
    });

    let opzioneToEdit = null;

    function openEditOpzione(opzione, card) {
        opzioneToEdit = { ...opzione, card };
        document.getElementById('editOpzioneId').value = opzione.idOpzione;
        document.getElementById('editNomeOpzione').value = opzione.nomeOpzione;
        document.getElementById('editPrezzoAggiuntivo').value = opzione.prezzoAggiuntivo;
        document.getElementById('editOpzioneModal').classList.remove('hidden');
    }

    function closeEditOpzione() {
        opzioneToEdit = null;
        document.getElementById('editOpzioneModal').classList.add('hidden');
    }

    document.getElementById('cancelEditOpzione').addEventListener('click', closeEditOpzione);
    document.getElementById('closeEditOpzioneModal').addEventListener('click', closeEditOpzione);

    /////salvare modifica
    document.getElementById('editOpzioneForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        if (!opzioneToEdit) return;

        const updatedOpzione = {
            idOpzione: document.getElementById('editOpzioneId').value,
            nomeOpzione: document.getElementById('editNomeOpzione').value,
            prezzoAggiuntivo: parseFloat(document.getElementById('editPrezzoAggiuntivo').value)
        };

        const token = localStorage.getItem('token');

        try {
            const res = await fetch(`http://localhost:8080/api/options/update`, {
                method: 'PUT',
                headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
                body: JSON.stringify(updatedOpzione)
            });

            if (!res.ok) {
                throw new Error(txt || `Errore server (${res.status})`);
            }

            // Chiudi modal e ricarica lista
            const data = await res.json();
            closeEditOpzione();
            loadOpzioni();
            showBanner(`Opzione "${data.nomeOpzione}" aggiornata con successo`, 'green');
        } catch (err) {
            showBanner('Errore modifica opzione: ' + err.message, 'red');
        }
    });




    // ===== HELPERS =====
    function showBanner(msg, type = 'green') {
        const colors = {
            green: 'bg-green-50 border-green-400 text-green-800',
            red: 'bg-red-50 border-red-400 text-red-800',
            yellow: 'bg-yellow-50 border-yellow-400 text-yellow-800'
        };
        topBanner.className = `${colors[type]} mb-4 p-4 border-l-4 rounded`;
        topBanner.innerHTML = `<div>${escapeHtml(msg)}</div>`;
        setTimeout(() => topBanner.className = 'hidden', 6000);
    }

    function escapeHtml(str) {
        if (!str && str !== 0) return '';
        return String(str).replace(/[&<>"']/g, s => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":"&#39;"}[s]));
    }

    function sampleAdmins() {
        return [
            { idAdmin: 1, nome: 'Mario Rossi', email: 'mario.rossi@example.com' },
            { idAdmin: 2, nome: 'Luca Bianchi', email: 'luca.bianchi@example.com' },
            { idAdmin: 3, nome: 'Anna Verdi', email: 'anna.verdi@example.com' }
        ];
    }

    function samplePrenotazioni() {
        return [
            { idPrenotazione: 101, nomeCliente: 'Giulia', dataInizio: '2025-09-20', dataFine: '2025-09-24', numeroStanza: 12, prezzoTotale: '€200' },
            { idPrenotazione: 102, nomeCliente: 'Marco', dataInizio: '2025-10-05', dataFine: '2025-10-10', numeroStanza: 5, prezzoTotale: '€350' }
        ];
    }

    function sampleCamere() {
        return [
            { idCamera: 1, numeroStanza: 101, tipo: 'Singola', prezzo: 50, disponibile: true },
            { idCamera: 2, numeroStanza: 102, tipo: 'Doppia', prezzo: 80, disponibile: false }
        ];
    }

});

