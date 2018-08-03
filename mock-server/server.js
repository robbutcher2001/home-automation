const express = require('express');
const app = express();
const port = 3000;

const { sayHello } = require('./mock-backend');
const lounge = require('./data/lounge.js');

app.get('/', (request, response) => {
  response.send('Mock backend');
});

app.get('/say-hello', (request, response) => {
  response.setHeader('Access-Control-Allow-Origin', '*');
  response.json(sayHello());
});

app.get('/lounge', (request, response) => {
  response.setHeader('Access-Control-Allow-Origin', '*');
  response.json(lounge());
});

app.listen(port, (err) => {
  if (err) {
    return console.log('Mock backend server couldn\'t start', err);
  }

  console.log(`Mock backend server listening on port ${port}`);
});