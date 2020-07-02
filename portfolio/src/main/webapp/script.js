// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

const FACTS = 
      ['👨 I am 19 years old',
      '🍣 My favorite food is sushi',
      '🐶 🐕 I have two dogs, their names are pipo and boogie.',
      '🎸 I play the guitar.',
      '🎱 My favorite number is eight.',
      '🎀 My favorite color on clothes is pink'];

/**
* Adds a random fact to the page.
*/
function addRandomFact() {
  // Pick a random fact.
  const fact = FACTS[Math.floor(Math.random() * FACTS.length)];

  // Add it to the page.
  const factContainer = document.getElementById('randomFact');
  factContainer.innerText = fact;
}

/**
* Retrieves comments when page loads.
*/
const getComments = () => {  
  const numberOfComments = document.getElementById('numberOfComments').value;
  fetch(`/list-comments?numberOfComments=${numberOfComments}`).then(response => response.json()).then((comments) => {
    const commentContainerElement = document.getElementById('comment-container');
    const titleElement = document.createElement("h2");

    // Clean element
    commentContainerElement.innerHTML = '';

    // If no comments, display an alert, else append comments to the container element.
    if (comments.length === 0) {
      titleElement.innerHTML = "There are no comments 😕, be the first!";
      commentContainerElement.appendChild(titleElement);
    }
    else {
      titleElement.innerHTML = "Last comments";
      commentContainerElement.appendChild(titleElement);
      appendComments(comments, commentContainerElement);
    }
  });
};

/**
* Creates HTML elements for each comment
* And appends them into the father element
* @param {Array<Object>} comments - Array of comments retrieved from servlet
* @param {Element} commentContainerElement - the target HTML element that will contain the comments.
*/
const appendComments = (comments, commentContainerElement) => {    
  Object.keys(comments).forEach(commentId => {
    // Create each element with its properties
    const userElement = document.createElement("h3");
    userElement.innerHTML = comments[commentId].user;

    const messageElement = document.createElement("p");
    messageElement.innerHTML = comments[commentId].message;

    const datePostedElement = document.createElement("div");
    datePostedElement.innerHTML = comments[commentId].datePosted;
    datePostedElement.classList.add("date");

    const commentElement =  document.createElement("div");
    commentElement.classList.add("comment");

    const trashIconElement = document.createElement("i");
    trashIconElement.classList.add("fa");
    trashIconElement.classList.add("fa-trash");
    trashIconElement.setAttribute("area-hidden", "true");

    const trashElement = document.createElement("a");
    trashElement.classList.add("trash-icon");
    const deleteFunctionParameter = "deleteComment(" + comments[commentId].id + ")";
    trashElement.setAttribute("onClick", deleteFunctionParameter);
    trashElement.setAttribute("alt", "Delete comments");
    trashElement.appendChild(trashIconElement);

    // append each element to the father element
    commentElement.appendChild(userElement);
    commentElement.appendChild(messageElement);
    commentElement.appendChild(datePostedElement);
    commentElement.appendChild(trashElement);

    // Make a final append to the comment container element
    commentContainerElement.appendChild(commentElement);
  });
};

/**
* Deletes a comment given the id of the comment
* @param {long} commentId - the id of the comment that will be deleted.
*/
const deleteComment = (commentId) => {
    fetch(`/delete-comment?commentId=${commentId}`).then(() => {
        getComments();
    });
};

// Retrieve comments when page is loaded
window.onload = getComments;