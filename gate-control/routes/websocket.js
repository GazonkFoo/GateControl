var config = require("../config");

module.exports = function (io, gate) {
    gate.addCallback(function (err, gateState) {
        if (err)
            io.emit("error", err);
        else
            io.emit("state", gateState);
    });

    io.on("connection", function (socket) {
        socket.emit("state", gate.getGateState());

        socket.on("buttonDown", function () {
            socket.broadcast.emit("gate:buttonDown");

            gate.pushButton(function (err) {
                if (err)
                    io.emit("error", err);
                else
                    io.emit("buttonUp");
            });
        });
    });
};
