var config = require("../config");
var gcm = require('node-gcm');

var sender = new gcm.Sender(config.gcmKey);
var regIds = [];

module.exports = function (app, gate) {
    gate.addCallback(function (err, gateState) {
        var message = new gcm.Message();

        if (err) {
            message.addNotification({
                title: 'Error',
                body: 'Abnormal data access',
                icon: 'ic_dialog_alert'
            });
        } else {
            message.addData('message', gateState);
            message.addData('timestamp', Date.now());
        }

        sender.send(message, regIds, function (err, result) {
            if (err) console.error(err);
            else    console.log(result);
        });
    });

    app.post('/register', function (req, res, next) {
        if (regIds.indexOf(req.body) < 0) {
            regIds.push(req.body);
        }
        res.status(200).set('Content-Type', 'text/plain').send('' + gate.getGateState());
    });

    app.post('/unregister', function (req, res, next) {
        var i = regIds.indexOf(req.body);
        if (i >= 0) {
            regIds.splice(i, 1);
        }
        res.status(204).end();
    });

    app.post('/buttonDown', function (req, res, next) {
        gate.pushButton(function (err) {
            if (err) {
                res.status(500).send(err);
            } else {
                res.status(204).end();
            }
        })
    });

    app.get('/state', function (req, res, next) {
        res.status(200).set('Content-Type', 'text/plain').send('' + gate.getGateState());
    });

    app.get('/registrations', function (req, res, next) {
        res.status(200).set('Content-Type', 'text/plain').send(regIds);
    });
};