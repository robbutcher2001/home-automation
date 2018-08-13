const jsend = require('../util/jsend');

const login = successful => {
  if (successful) {
    return jsend('attempt', `Hello, you've logged in.`);
  }
  return jsend('attempt', `Wrong creds, try again`);
};

module.exports = login;
