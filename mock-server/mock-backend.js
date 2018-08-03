const sayHello = () => {
  const randomNumber = Math.floor(Math.random() * Math.floor(1000));

  return {
    "status": 'success',
    "newHello": `Server said hello with random number ${randomNumber}`
  }
};

module.exports = {
  sayHello
};
