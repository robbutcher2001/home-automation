const express = require('express');
const app = express();
const port = 3000;

const apartment = require('./data/apartment');
const lounge = require('./data/lounge');
const patio = require('./data/patio');
const robRoom = require('./data/robRoom');
const study = require('./data/study');
const hallway = require('./data/hallway');
const bathroom = require('./data/bathroom');

app.get('/', (request, response) => {
  response.send('Mock backend');
});

app.get('/deviceStatus/apartment', (request, response) => {
  response.setHeader('Access-Control-Allow-Origin', '*');
  response.json(apartment());
});

app.get('/deviceStatus/lounge', (request, response) => {
  response.setHeader('Access-Control-Allow-Origin', '*');
  response.json(lounge());
});

app.get('/deviceStatus/patio', (request, response) => {
  response.setHeader('Access-Control-Allow-Origin', '*');
  response.json(patio());
});

app.get('/deviceStatus/rob_room', (request, response) => {
  response.setHeader('Access-Control-Allow-Origin', '*');
  response.json(robRoom());
});

app.get('/deviceStatus/scarlett_room', (request, response) => {
  response.setHeader('Access-Control-Allow-Origin', '*');
  response.json(study());
});

app.get('/deviceStatus/hallway', (request, response) => {
  response.setHeader('Access-Control-Allow-Origin', '*');
  response.json(hallway());
});

app.get('/deviceStatus/bathroom', (request, response) => {
  response.setHeader('Access-Control-Allow-Origin', '*');
  response.json(bathroom());
});

app.listen(port, (err) => {
  if (err) {
    return console.log('Mock backend server couldn\'t start', err);
  }

  console.log(`Mock backend server listening on port ${port}`);
});
