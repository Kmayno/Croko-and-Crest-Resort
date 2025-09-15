document.addEventListener('DOMContentLoaded', function () {
    const steps = document.querySelectorAll('.wizard-step');
    const progressBar = document.querySelector('.progress-bar');
    const airplaneIcon = document.querySelector('.airplane-icon');
    const nextButtons = document.querySelectorAll('.btn-next');
    const prevButtons = document.querySelectorAll('.btn-prev');
    const form = document.getElementById('preventivatore-form');

    let currentStep = 0;

    const prices = {
        'Parigi': 300,
        'Roma': 100,
        'Madrid': 200,
        'Bangkok': 250
    };

    function updateProgressBar() {
        // Calcola la percentuale di avanzamento basandosi sullo step corrente
        // e sul numero totale di transizioni (steps.length - 1)
        const progressPercentage = (currentStep / (steps.length - 1)) * 100;
        progressBar.style.width = progressPercentage + '%';
        progressBar.setAttribute('aria-valuenow', progressPercentage);

        // Posiziona l'icona dell'aereo. L'offset di -15px serve a centrare l'aereo
        // rispetto alla fine della barra di avanzamento.
        airplaneIcon.style.left = `calc(${progressPercentage}% - 15px)`;
    }

    function showStep(stepIndex) {
        steps.forEach((step, index) => {
            step.classList.toggle('active', index === stepIndex);
        });
        currentStep = stepIndex;
        updateProgressBar();
    }

    function validateStep(stepIndex) {
        const currentStepElement = steps[stepIndex];
        const inputs = currentStepElement.querySelectorAll('input, select');
        let isValid = true;

        inputs.forEach(input => {
            if (!input.checkValidity()) {
                input.classList.add('is-invalid');
                isValid = false;
            } else {
                input.classList.remove('is-invalid');
            }
        });

        // Aggiunge la classe per mostrare i messaggi di errore
        currentStepElement.classList.add('was-validated');

        return isValid;
    }

    function calculatePrice() {
        const destination = form.elements['destination'].value;
        const duration = parseInt(form.elements['duration'].value, 10);
        const participants = parseInt(form.elements['participants'].value, 10);

        if (destination && duration > 0 && participants > 0) {
            const basePrice = prices[destination];
            const totalPrice = basePrice * duration * participants;

            document.getElementById('total-price').textContent = `€ ${totalPrice}`;
            document.getElementById('price-details').textContent = 
                `Destinazione: ${destination} (€${basePrice}) x ${duration} giorni x ${participants} partecipanti`;
        } else {
            document.getElementById('total-price').textContent = '€ 0';
            document.getElementById('price-details').textContent = 'Completa i dati per calcolare il prezzo.';
        }
    }

    nextButtons.forEach(button => {
        button.addEventListener('click', () => {
            if (validateStep(currentStep)) {
                if (currentStep < steps.length - 1) {
                    showStep(currentStep + 1);
                }
            }
        });
    });

    prevButtons.forEach(button => {
        button.addEventListener('click', () => {
            if (currentStep > 0) {
                showStep(currentStep - 1);
            }
        });
    });

    form.addEventListener('submit', function (event) {
        event.preventDefault();
        if (validateStep(currentStep)) {
            calculatePrice();
            // Qui potresti inviare i dati o mostrare un riepilogo finale
            alert('Preventivo calcolato con successo!');
        }
    });

    // Calcola il prezzo al primo step quando i valori cambiano
    const firstStepInputs = steps[0].querySelectorAll('select, input');
    firstStepInputs.forEach(input => {
        input.addEventListener('change', calculatePrice);
    });

    // Inizializzazione
    showStep(0);
    calculatePrice(); // Calcola il prezzo iniziale (che sarà 0)
});