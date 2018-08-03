module.exports = (room, json) => {
  const response = {
    "errorText": null,
    "status": "success"
  };

  response[room] = json;

  return response;
};
