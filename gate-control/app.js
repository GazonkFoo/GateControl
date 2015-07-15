#!/usr/bin/env node
var config = require("./config");

var http = require("http");
var express = require("express");
var bodyParser = require("body-parser");
var serveStatic = require("serve-static");
var path = require("path");
var socketIO = require("socket.io");
var Gate = require("./gate");

var app = express();
var server = http.Server(app);
var io = socketIO(server, {path: config.basePath +"/socket.io"});
app.use(bodyParser.text());
app.use(config.basePath, serveStatic(path.join(__dirname, "public")));

require("./routes")(app, io, new Gate());

server.listen(config.port, function() {
	console.log("Server listening on port "+ config.port);
});

