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
