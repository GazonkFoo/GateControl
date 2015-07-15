var config = require("../config");
var express = require('express');

module.exports = function(app, io, gate) {
	require('./websocket')(io, gate);
	require('./android')(getRoute(app, config.androidPath), gate);
};

function getRoute(app, path) {
	var route = express.Router();
	app.use(path, route);
	return route;
}
