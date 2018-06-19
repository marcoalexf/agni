async function info(){
    var info = await informations();

}

function informations(){
    return new Promise( resolve => {
        console.log("informations1");
        var obj;
        var token = window.localStorage.getItem('token');
        var d = new Date();
        var t = d.getTime();
        // var expirationData = JSON.parse(token).expirationData;
        // console.log(token);
        // console.log(t);
        // console.log(expirationData);

        if(token != null){
            var uname = JSON.parse(token).username;
            var tokenObj = JSON.parse(token);

            var user = {
                "username": uname,
                "token": tokenObj
            }

            var xmlHttp = new XMLHttpRequest();
            xmlHttp.open( "POST", "https://custom-tine-204615.appspot.com/rest/profile/", true);
            xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            var myJSON = JSON.stringify(user);
            xmlHttp.send(myJSON);

            xmlHttp.onreadystatechange = function() {
                if (xmlHttp.readyState === XMLHttpRequest.DONE){
                    if(xmlHttp.status === 200){
                        console.log("informations2");
                        var response = xmlHttp.response;
                        console.log("XML response: " + response);
                        obj = JSON.parse(response);
                        console.log(obj);

                        console.log(uname.charAt(0));

                        resolve(obj);
                    }

                    else{
                        console.log("tempo expirado");
                        window.localStorage.removeItem('token');
                        // document.getElementById("tologin").click();
                    }
                }
            }.bind(this)
        }
        else{
            console.log("Sem sessao iniciada");
            // document.getElementById("tologin").click();

        }
    });

}