const express = require('express');
const cookieParser = require('cookie-parser');
const bodyParser = require('body-parser');
const app = express();
const port = 3000;

const login = require('./data/login');
const apartment = require('./data/apartment');
const lounge = require('./data/lounge');
const patio = require('./data/patio');
const robRoom = require('./data/robRoom');
const study = require('./data/study');
const hallway = require('./data/hallway');
const bathroom = require('./data/bathroom');

const FAKE_COOKIE_NAME = 'RobsApartment';
const FAKE_COOKIE_VALUE = 'blah';

app.use(cookieParser());
app.use(bodyParser.json());

const wrapResponse = response => {
  response.setHeader('Access-Control-Allow-Origin', 'http://localhost:8080');
  response.setHeader('Access-Control-Allow-Credentials', true);
  response.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  return response;
};

app.get('/', (request, response) => {
  response.send('Mock backend');
});

app.get('/verifyEngineOnline', (request, response) => {
  if (FAKE_COOKIE_VALUE === request.cookies[FAKE_COOKIE_NAME]) {
    setTimeout(() => {
      wrapResponse(response).status(200).send();
    }, 2000);
  }
  else {
    setTimeout(() => {
      wrapResponse(response).status(406).send();
    }, 3000);
  }
});

app.options('/login', (request, response) => {
  wrapResponse(response).status(200).send();
});

app.post('/login', (request, response) => {
  const uname = request.body.uname;
  const pword = request.body.pword;
  if (uname === 'a' && pword === 'a') {
    response.cookie(FAKE_COOKIE_NAME, FAKE_COOKIE_VALUE);
    wrapResponse(response).status(200).json(login(true));
  }
  else {
    wrapResponse(response).status(401).send(login(false));
  }
});

app.get('/deviceStatus/apartment', (request, response) => {
  wrapResponse(response).json(apartment());
});

app.get('/deviceStatus/lounge', (request, response) => {
  wrapResponse(response).json(lounge());
});

app.get('/deviceStatus/patio', (request, response) => {
  wrapResponse(response).json(patio());
});

app.get('/deviceStatus/rob_room', (request, response) => {
  wrapResponse(response).json(robRoom());
});

app.get('/deviceStatus/scarlett_room', (request, response) => {
  wrapResponse(response).json(study());
});

app.get('/deviceStatus/hallway', (request, response) => {
  wrapResponse(response).json(hallway());
});

app.get('/deviceStatus/bathroom', (request, response) => {
  wrapResponse(response).json(bathroom());
});

app.listen(port, (err) => {
  if (err) {
    return console.log('Mock backend server couldn\'t start', err);
  }

  console.log(`Mock backend server listening on port ${port}`);
});
