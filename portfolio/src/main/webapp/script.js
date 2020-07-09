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
  const typeOfComments = document.getElementById('typeOfComments').value;
  fetch(`/list-comments?numberOfComments=${numberOfComments}&orderBy=${typeOfComments}`
  ).then(response => {
    if (response.redirected) {
      window.location.href = response.url;
      return;
    }
    response.json().then((comments) => {
      const commentContainerElement = document.getElementById('comment-container');
      const titleElement = document.createElement('h2');

      // Clean element
      commentContainerElement.innerHTML = '';

      // If no comments, display an alert, else append comments to the container element.
      if (comments.length === 0) {
        titleElement.innerHTML = "There are no comments 😕, be the first!";
        commentContainerElement.appendChild(titleElement);
      } else {
        titleElement.innerHTML = "Last comments";
        commentContainerElement.appendChild(titleElement);
        appendComments(comments, commentContainerElement);
      }
    });
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
    const comment = comments[commentId];
    // Create each element with its properties
    const userElement = document.createElement('h3');
    userElement.innerHTML = comment.username;

    const votesContainerElement = document.createElement('div');
    votesContainerElement.classList.add('votes');
    const numberOfVotes = document.createElement('p');
    numberOfVotes.innerHTML = comment.votes;

    const upVoteIcon = document.createElement('i');
    upVoteIcon.classList.add('fa');
    upVoteIcon.classList.add('fa-thumbs-up');
    upVoteIcon.setAttribute('area-hidden', 'true');

    const upVoteElement = document.createElement('a');
    upVoteElement.classList.add('icon');
    upVoteElement.classList.add('up');
    const voteUpFunctionParameter = "voteComment(" + comment.id + ",true)";
    upVoteElement.setAttribute('onClick', voteUpFunctionParameter);
    upVoteElement.setAttribute('alt', 'Thumbs up comment');
    upVoteElement.appendChild(upVoteIcon);

    const downVoteIcon = document.createElement('i');
    downVoteIcon.classList.add('fa');
    downVoteIcon.classList.add('fa-thumbs-down');
    downVoteIcon.setAttribute('area-hidden', 'true');

    const downVoteElement = document.createElement('a');
    downVoteElement.classList.add('icon');
    downVoteElement.classList.add('down');
    const voteDownFunctionParameter = "voteComment(" + comment.id + ",false)";
    downVoteElement.setAttribute('onClick', voteDownFunctionParameter);
    downVoteElement.setAttribute('alt', 'Thumbs down comment');
    downVoteElement.appendChild(downVoteIcon);

    votesContainerElement.appendChild(numberOfVotes);
    votesContainerElement.appendChild(upVoteElement);
    votesContainerElement.appendChild(downVoteElement);


    const messageElement = document.createElement('p');
    messageElement.classList.add('message');
    messageElement.innerHTML = comment.message;

    const datePostedElement = document.createElement('div');
    datePostedElement.innerHTML = comment.datePosted;
    datePostedElement.classList.add('date');

    const commentElement = document.createElement('div');
    commentElement.classList.add('comment');
    const trashIconElement = document.createElement('i');
    const trashElement = document.createElement('a');

    //Only show delete icon if the comment was posted by the same user
    if (comment.postedBySameUser) {
      trashIconElement.classList.add('fa');
      trashIconElement.classList.add('fa-trash');
      trashIconElement.setAttribute('area-hidden', 'true');

      trashElement.classList.add('trash-icon');
      const deleteFunctionParameter = "deleteComment(" + comment.id + ")";
      trashElement.setAttribute('onClick', deleteFunctionParameter);
      trashElement.setAttribute('alt', 'Delete comments');
      trashElement.appendChild(trashIconElement);
    }

    // append each element to the father element
    commentElement.appendChild(userElement);
    commentElement.appendChild(votesContainerElement);
    commentElement.appendChild(messageElement);
    commentElement.appendChild(datePostedElement);
    if (comment.postedBySameUser) {
      commentElement.appendChild(trashElement);
    }
    // Make a final append to the comment container element
    commentContainerElement.appendChild(commentElement);
  });
};

/**
 * Deletes a comment given the id of the comment
 * @param {long} commentId - the id of the comment that will be deleted.
 */
const deleteComment = (commentId) => {
  fetch(`/delete-comment?commentId=${commentId}`).then(response => {
    if (response.redirected) {
      window.location.href = response.url;
      return;
    }
    getComments();
  });
};

/**
 * votes a comment given the id of the comment and
 * type of vote, true = +1 , false = -1
 * @param {long} commentId - the id of the comment that will be voted.
 * @param {boolean} typeOfVote - the type of vote of the comment that will be voted.
 */
const voteComment = (commentId, typeOfVote) => {
  fetch(`/vote-comment?commentId=${commentId}&vote=${typeOfVote}`).then(response => {
    if (response.redirected) {
      window.location.href = response.url;
      return;
    }
    getComments();
  });
};

/**
 * Retrieves user's data and auth status
 * There exists three cases, when user is logged out (show log in button)
 * user logged in but has no nickname set (show register nickname form)
 * and when user has a nickname and is logged in. This method handles
 * this three cases by fetching results from the AuthServlet
 */
const getUserData = () => {
  fetch('/auth').then(response => response.json()).then((userData) => {
    const userContainerElement = document.getElementById('user-container');
    // Clean element.
    userContainerElement.innerHTML = '';

    if (userData.userStatus === "HAS_NICKNAME") {
      const nameElement = document.createElement('p');
      const logoutElement = document.createElement('a');

      // Insert user data into HTML body.
      nameElement.innerHTML = 'Hi ' + userData.name + '!';
      userContainerElement.appendChild(nameElement);
      logoutElement.setAttribute('href', userData.url);
      logoutElement.innerHTML = 'Click here to logout!';
      userContainerElement.appendChild(logoutElement);

      // Once the user is registed, show the comment section
      getComments();
    } else if (userData.userStatus === "LOGGED_IN") {
      const loginMessageElement = document.createElement('p');
      loginMessageElement.innerHTML = 'Please register your nickname bellow ' +
        'to access the comment section';
      userContainerElement.appendChild(loginMessageElement);

      const formElement = document.createElement('form');
      formElement.setAttribute('method', 'POST');
      formElement.setAttribute('action', '/auth');

      const nameInputElement = document.createElement('input');
      nameInputElement.setAttribute('class', 'text-input');
      nameInputElement.setAttribute('name', 'username');
      nameInputElement.setAttribute('placeholder', 'username');

      const submitButtonElement = document.createElement('input');
      submitButtonElement.setAttribute('type', 'submit');
      submitButtonElement.setAttribute('value', 'Register');
      submitButtonElement.setAttribute('class', 'btn');

      formElement.appendChild(nameInputElement);
      formElement.appendChild(submitButtonElement);

      userContainerElement.appendChild(formElement);
    } else {
      // Insert user data into HTML body
      const loginMessageElement = document.createElement('h3');
      const loginElement = document.createElement('a');
      loginMessageElement.innerHTML = 'Please log in to see the comment section';
      userContainerElement.appendChild(loginMessageElement);
      loginElement.setAttribute('href', userData.url);
      loginElement.innerHTML = 'Click here to log in';
      userContainerElement.appendChild(loginElement);
    }
  });
};

// Retrieve comments and user's data when page is loaded
window.onload = () => {
  getUserData();
};