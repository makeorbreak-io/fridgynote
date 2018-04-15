document.addEventListener("DOMContentLoaded", function(event) {
    function makeRequest() {
        setTimeout(function() {
            request();
        }, 1000);
    }

    function request() {
        fetch('http://localhost:3000/notes/receive', {
            mode: 'cors'
        }).then((note)=>{
            return note.json();
        }
        ).then((noteData)=>{
            if (noteData) {
                fillData(noteData.title, noteData.body)
            }
            makeRequest();
        }
        ).catch((err)=>{
            console.log(err)
            alert(err);
        }
        )
    }

    function fillData(title, body) {

        document.querySelector("#title").innerHTML = title;

        document.querySelector("#body").innerHTML = body;
    }

    makeRequest();

});
