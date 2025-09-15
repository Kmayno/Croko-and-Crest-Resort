// ========== FILTRO DESTINAZIONI (per principianti) ==========

// PASSO 1: Aspettiamo che la pagina si carichi completamente
document.addEventListener('DOMContentLoaded', function() {
    
    // PASSO 2: Troviamo tutti i bottoni filtro
    const bottoni = document.querySelectorAll('.filtro-btn');
    
    // PASSO 3: Troviamo tutte le card delle destinazioni
    const cards = document.querySelectorAll('.card-destinazione');
    
    // PASSO 4: Per ogni bottone, aggiungiamo un "ascoltatore" di click
    bottoni.forEach(function(bottone) {
        
        // Quando qualcuno clicca questo bottone...
        bottone.addEventListener('click', function() {
            
            // PASSO 5: Prendiamo il tipo di filtro dal bottone cliccato
            const filtroSelezionato = bottone.getAttribute('data-filtro');
            
            // PASSO 6: Rimuoviamo la classe "active" da tutti i bottoni
            bottoni.forEach(function(btn) {
                btn.classList.remove('active');
            });
            
            // PASSO 7: Aggiungiamo la classe "active" solo al bottone cliccato
            bottone.classList.add('active');
            
            // PASSO 8: Filtriamo le card
            filtraCard(filtroSelezionato);
        });
    });
    
    // FUNZIONE PER FILTRARE LE CARD
    function filtraCard(filtro) {
        
        // PASSO 9: Per ogni card, decidiamo se mostrarla o nasconderla
        cards.forEach(function(card) {
            
            // Prendiamo la categoria di questa card
            const categoriaCard = card.getAttribute('data-categoria');
            
            // Se il filtro è "tutti" OPPURE la categoria corrisponde...
            if (filtro === 'tutti' || categoriaCard === filtro) {
                // ...mostra la card
                card.classList.remove('card-nascosta');
            } else {
                // ...altrimenti nascondi la card
                card.classList.add('card-nascosta');
            }
        });
    }
});