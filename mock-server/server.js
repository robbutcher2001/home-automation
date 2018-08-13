const express = require('express');
const cookieParser = require('cookie-parser');
const app = express();
const port = 3000;

const apartment = require('./data/apartment');
const lounge = require('./data/lounge');
const patio = require('./data/patio');
const robRoom = require('./data/robRoom');
const study = require('./data/study');
const hallway = require('./data/hallway');
const bathroom = require('./data/bathroom');

app.use(cookieParser());

const wrapResponse = response => {
  response.setHeader('Access-Control-Allow-Origin', 'http://localhost:8080');
  response.setHeader('Access-Control-Allow-Credentials', true);

  return response;
};

app.get('/', (request, response) => {
  response.send('Mock backend');
});

app.get('/verifyEngineOnline', (request, response) => {
  setTimeout(() => {
    wrapResponse(response).status(406).send();
  }, 3000);
});

app.get('/deviceStatus/apartment', (request, response) => {
  wrapResponse(response).json(apartment());
});

app.get('/deviceStatus/lounge', (request, response) => {
  console.log('Cookies: ', request.cookies);
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
