// window.addEventListener('beforeunload', () => {
//     stompClient.send("/app/unregister", {}, getCurrentUser());
// });
//
// function getCurrentUser() {
//     const element = document.getElementById('currentUser');
//     return element ? element.getAttribute('data-username') : null;
// }

document.addEventListener('DOMContentLoaded', function() {
    connect();

    let cards = document.querySelectorAll('.diaryCard');
    let btnLeft = document.querySelector('.navButton.left');
    let btnRight = document.querySelector('.navButton.right');

    let currentIndex = 0;

    function updateCardsList() {
        cards = document.querySelectorAll('.diaryCard');
    }

    function goToCard(index) {
        if (cards.length === 0) return;

        if (index < 0) index = cards.length - 1;
        if (index >= cards.length) index = 0;

        currentIndex = index;
        cards.forEach((card, i) => {
            card.style.transform = `translateX(-${currentIndex * 100}%)`;
        });
    }

    btnLeft.addEventListener('click', () => goToCard(currentIndex - 1));
    btnRight.addEventListener('click', () => goToCard(currentIndex + 1));

    document.addEventListener('keydown', (e) => {
        if (e.key === 'ArrowLeft') goToCard(currentIndex - 1);
        if (e.key === 'ArrowRight') goToCard(currentIndex + 1);
    });

        const createNewDiaryCard = document.getElementById('createNewDiaryCard');
        createNewDiaryCard.addEventListener('click', function () {
            const createDiaryCard = document.getElementById('createDiaryCard');
         const cardsContainer = document.getElementById(`cardsContainer`);
         const existingDivs = cardsContainer.querySelectorAll('.diaryCard.exist').length;
         if (existingDivs > 10) {
             createDiaryCard.style.display = "none";
             alert("Число карточек ограничено! Удалите ненужные.");
             return;
         }

        const now = formatDate();
         const cardId = 'new_' + Date.now();

        const newDiaryCard = document.createElement('div');
        newDiaryCard.className = 'diaryCard exist';
        newDiaryCard.id = cardId;
        newDiaryCard.setAttribute('data-id', cardId);
        newDiaryCard.innerHTML=`
        <div class="cardTitle">
                    <input class="title" type="text" value="Заголовок">
                    <p class="timeCreateCard">${now}</p>
                </div>

                <div class="cardBody" id="cardBody${cardId}">
                    <div class="cardItemsContainer" id="cardItemsContainer${cardId}">
                    </div>
                    <button class="createNewParagraph" data-id="${cardId}"><img src="/img/add.png"
                                                            height="32px"
                                                            width="32px"></button>
                <div class="cardAction">
                <button class="saveCard" id="saveCard${cardId}"><img src="/img/save.png"
                                                       height="32px"
                                                       width="32px"></button>
                <div class="empty"></div>
                <button class="deleteCard" id="deleteCard"><img src="/img/delete.png"
                                                      height="32px"
                                                      width="32px"></button>
                </div>
                </div>           
        `;
         cardsContainer.insertBefore(newDiaryCard, createNewDiaryCard.parentElement);

         updateCardsList();
         goToCard(cards.length - 1);
     });

     document.addEventListener('click', function (e){
         if (e.target.closest('.createNewParagraph')){
             const button = e.target.closest('.createNewParagraph');
             const cardId = button.dataset.id;
             const paragraphId = 'new_' + Date.now();

             const cardItemsContainer = document.getElementById(`cardItemsContainer${cardId}`);
             const existingDivs = cardItemsContainer.querySelectorAll('.parentParagraph').length;
             if (existingDivs > 16) {
                 alert("Число задач на карточку ограничено! Продолжите список в другой.");
                 return;
             }

             const parentParagraph = document.createElement('div');
             parentParagraph.className = `parentParagraph`;
             parentParagraph.id=`parentParagraph${paragraphId}`;
             parentParagraph.innerHTML = `
                            <input class="taskStatus" type="checkbox">
                            <input class="task" type="text">
                            <button class="deleteParagraph" data-id="${paragraphId}" data-card-id="${cardId}"><img src="/img/close.png" height="13px" width="13px"></button>
             `;

             cardItemsContainer.appendChild(parentParagraph);
         }

         if (e.target.closest('.deleteParagraph')) {
             const button = e.target.closest('.deleteParagraph');
             const paragraphId = button.dataset.id;
             const cardId = button.dataset.cardId;
             document.getElementById(`saveCard${cardId}`).className = "saveCard";

            document.getElementById(`parentParagraph${paragraphId}`).remove();
         }

         if (e.target.closest('.saveCard')) {
             const button = e.target.closest('.saveCard');
            button.className = "saveCard inactive";
             const cardId = button.id.replace('saveCard', '');

             const cardData = collectCardData(cardId);
             saveCardToServer(cardData);
         }

         if (e.target.classList.contains('title')) {
             const card = e.target.closest('.diaryCard.exist');
             const cardId = card.dataset.id;
             document.getElementById(`saveCard${cardId}`).className = "saveCard";
         }

         if (e.target.classList.contains('task')) {
             const parentParagraph = e.target.closest('.parentParagraph');
             const card = e.target.closest('.diaryCard.exist');
             const cardId = card.dataset.id;
             document.getElementById(`saveCard${cardId}`).className = "saveCard";

         }

         if (e.target.classList.contains('taskStatus')) {
             const parentParagraph = e.target.closest('.parentParagraph');
             const card = e.target.closest('.diaryCard.exist');
             const cardId = card.dataset.id;
             document.getElementById(`saveCard${cardId}`).className = "saveCard";

         }

         if (e.target.closest('.deleteCard')) {
             const button = e.target.closest('.deleteCard');
             const card = button.closest('.diaryCard.exist');
             const cardId = card.id;


             if (confirm('Вы уверены, что хотите удалить эту карточку?')) {
                 deleteCardOnServer(cardId);
                 card.remove();
                 updateCardsList();
             }
         }
     });

});

function collectCardData(cardId) {
    const card = document.getElementById(cardId);
    if (!card) {return null;}

    const userId = document.getElementById('currentUser').dataset.id;

    const isNewCard = cardId.startsWith('new_');

    const cardData = {
        id: isNewCard ? null : cardId,
        userId: parseInt(userId),
        title: card.querySelector('.title').value,
        date: isNewCard ? new Date().toISOString().split('T')[0] : null,
        diaryParagraphs: []
    };

    const paragraphsContainer = document.getElementById(`cardItemsContainer${cardId}`);
    if (paragraphsContainer) {
        const paragraphs = paragraphsContainer.querySelectorAll('.parentParagraph');

        paragraphs.forEach(paragraph => {

            const taskInput = paragraph.querySelector('.task');
            const content = taskInput.value.trim();

            if (!content) return;

            const paragraphId = paragraph.id.replace('parentParagraph', '');
            const isNewParagraph = paragraphId.startsWith('new_');

            cardData.diaryParagraphs.push({
                id: isNewParagraph ? null : parseInt(paragraphId),
                content: paragraph.querySelector('.task').value,
                isReady: paragraph.querySelector('.taskStatus').checked
            });
        });
    }

    return cardData;
}

function saveCardToServer(cardData) {
    stompClient.send(
        "/app/saveCard",
        {},
        JSON.stringify(cardData)
    );
}

function deleteCardOnServer(cardId) {
    if (cardId.startsWith('new_')) return;

    const cardData = {
        id: parseInt(cardId),
        userId: parseInt(document.getElementById('currentUser').dataset.id)
    };

    stompClient.send(
        "/app/deleteCard",
        {},
        JSON.stringify(cardData)
    );
}

function formatDate() {
    const date = new Date();
    return date.toLocaleDateString([], {year: 'numeric', month:'2-digit', day:'2-digit'})
}