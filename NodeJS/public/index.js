$(function() {
	var socket = connectIO();
	var connected = false;

	socket.on("state", function(state) {
		$("#lblUnknown").addClass("hidden");
		$("#lblClosed").toggleClass("hidden", state == 0);
		$("#lblOpen").toggleClass("hidden", state == 1);
		connected = true;
	});

	socket.on("disconnect", function() {
		$("#lblUnknown").removeClass("hidden");
		$("#lblClosed").addClass("hidden");
		$("#lblOpen").addClass("hidden");
		connected = false;
	});

	$("#btnOpen").click(function() {
		$("#btnOpen").button("loading");
		socket.emit("buttonDown");
	});

	socket.on("buttonDown", function() {
		$("#btnOpen").button("loading");
	});
	
	socket.on("buttonUp", function() {
		$("#btnOpen").button("reset");
	});

	socket.on("error", function(err) {
		$.growl({
			title: "Fehler",
			message: err,
			icon: "glyphicon glyphicon-exclamation-sign"}, {
			type: "danger",
			position: { from: "bottom", align: "center" },
			template: { title_divider: "<p></p>" }
		});
	});

	function connectIO(namespace) {
		var path = window.location.pathname;
		path = path.substring(0, path.lastIndexOf("/") + 1);
		path = path + "socket.io";

		var url = window.location.origin;
		if(namespace)
			url = url +'/'+ namespace;

		return io.connect(url, {path: path});
	}
});

