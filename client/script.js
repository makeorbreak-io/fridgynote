document.addEventListener("DOMContentLoaded", function(event) {
    function start(websocketServerLocation) {
        ws = new WebSocket(websocketServerLocation);
        ws.onopen = function() {
            console.log('socket open');
        }
        ;
        ws.onmessage = function(evt) {
            const textNote = JSON.parse(evt.data);
            console.log(textNote);
            fillData(textNote.title,textNote.body)
        }
        ;
        ws.onclose = function() {
            // Try to reconnect in 5 seconds
            setTimeout(function() {
                start(websocketServerLocation)
            }, 1000);
        }
        ;
    }

    function fillData(title, body) {

        document.querySelector("#title").innerHTML = title;

        document.querySelector("#body").innerHTML = body;
    }

    //start('ws://localhost:3000')
    start('ws://fridgynote.herokuapp.com/')

});
