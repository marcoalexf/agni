// This file is shared across the demos.

import React from 'react';
import { ListItem, ListItemIcon, ListItemText } from 'material-ui/List';
import HomeIcon from '@material-ui/icons/Home';
import NewsIcon from '@material-ui/icons/Public';
import ProfileIcon from '@material-ui/icons/PermIdentity';
import RegistPIcon from '@material-ui/icons/NoteAdd';
import OperationsIcon from '@material-ui/icons/ViewList';
import MapIcon from '@material-ui/icons/Place';
import StatsIcon from '@material-ui/icons/Poll';
import LogoutIcon from '@material-ui/icons/ExitToApp';
import {Link} from 'react-router-dom';

/*handleLogout = () => {
    var token = window.localStorage.getItem('token');
    var uname = JSON.parse(token).username;
    var tokenID = JSON.parse(token).tokenID;

    var data = {
        "user": uname,
        "token": tokenID
    }

    if(user!= null && token != null){
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open( "POST", "http://localhost:8080/rest/logout");
        xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        var myJSON = JSON.stringify(data);
        xmlHttp.send(myJSON);

        xmlHttp.onreadystatechange = function() {
            if(xmlHttp.readyState == XMLHttpRequest.DONE && xmlHttp.status == 200) {
                var response = xmlHttp.responseText;
                console.log("XML response: " + response);
                window.localStorage.removeItem('token');
                //document.location.href = 'initialpage.html';
            }
        }
    }
}*/

export const mainMenuListItems = (
    <div>
        <ListItem component={Link}
                  to="/">
            <ListItemIcon>
                <HomeIcon />
            </ListItemIcon>
            <ListItemText primary="Página Inicial" />
        </ListItem>
        <ListItem component={Link}
                  to="/noticias">
            <ListItemIcon>
                <NewsIcon />
            </ListItemIcon>
            <ListItemText primary="Noticias" />
        </ListItem>
        <ListItem component={Link}
                  to="/perfil">
            <ListItemIcon>
                <ProfileIcon />
            </ListItemIcon>
            <ListItemText primary="Perfil" />
        </ListItem>
        <ListItem component={Link}
                  to="/registarproblema">
            <ListItemIcon>
                <RegistPIcon />
            </ListItemIcon>
            <ListItemText primary="Registar Problema" />
        </ListItem>
        <ListItem component={Link}
                  to="/operacoes">
            <ListItemIcon>
                <OperationsIcon />
            </ListItemIcon>
            <ListItemText primary="Operacoes" />
        </ListItem>
        <ListItem component={Link}
                  to="/mapa">
            <ListItemIcon>
                <MapIcon />
            </ListItemIcon>
            <ListItemText primary="Mapa" />
        </ListItem>
        <ListItem component={Link}
                  to="/estatisticas">
            <ListItemIcon>
                <StatsIcon />
            </ListItemIcon>
            <ListItemText primary="Estatisticas" />
        </ListItem>
    </div>
);

export const logoutItem = (
    <div>
        <ListItem button>
            <ListItemIcon>
                <LogoutIcon />
            </ListItemIcon>
            <ListItemText primary="Logout" />
        </ListItem>
    </div>

);
