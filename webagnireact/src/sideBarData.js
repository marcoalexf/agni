// This file is shared across the demos.

import React from 'react';
import { ListItem, ListItemIcon, ListItemText } from 'material-ui/List';
import NewsIcon from '@material-ui/icons/Public';
import ProfileIcon from '@material-ui/icons/PermIdentity';
import RegistPIcon from '@material-ui/icons/NoteAdd';
import OperationsIcon from '@material-ui/icons/ViewList';
import MapIcon from '@material-ui/icons/Place';
import StatsIcon from '@material-ui/icons/Poll';
import LogoutIcon from '@material-ui/icons/ExitToApp';

export const mainMenuListItems = (
    <div>
        <ListItem button>
            <ListItemIcon>
                <NewsIcon />
            </ListItemIcon>
            <ListItemText primary="Noticias" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <ProfileIcon />
            </ListItemIcon>
            <ListItemText primary="Perfil" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <RegistPIcon />
            </ListItemIcon>
            <ListItemText primary="Registar Problema" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <OperationsIcon />
            </ListItemIcon>
            <ListItemText primary="Operacoes" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <MapIcon />
            </ListItemIcon>
            <ListItemText primary="Mapa" />
        </ListItem>
        <ListItem button>
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
