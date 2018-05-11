import React, { Component } from 'react';
import Table, { TableBody, TableCell, TableHead, TableRow } from 'material-ui/Table';
import operationsData from './operationsData';
import Typography from 'material-ui/Typography';
import InfoIcon from '@material-ui/icons/EventNote';
import IconButton from 'material-ui/IconButton';
import PropTypes from 'prop-types';
import {withStyles} from "material-ui/styles/index";
import {Link} from "react-router-dom";
import Button from 'material-ui/Button';

const styles = theme => ({
    title:{
        margin:20,
    },
    root: {
        width: '100%',
        marginTop: theme.spacing.unit * 3,
        overflowX: 'auto',
    },
    table: {
        minWidth: 700,
    },
});

class Operations extends React.Component {

    state = {
        occurrences: null,
    };

    loadOperations = () => {
        console.log("aqui");
        var obj;
        var token = window.localStorage.getItem('token');

        if(token != null) {
            var uname = JSON.parse(token).username;
            var tokenObj = JSON.parse(token);

            // if(tokenObj.expirationData){
            //  tratar para o caso do token ter expirado
            // }

            //MUDAR SHOW PRIVATE DEPOIS
            var user = {
                "username": uname,
                "token": tokenObj,
                "showPrivate": true //MUDAR ISTO DEPOIS
            }

            var xmlHttp = new XMLHttpRequest();
            xmlHttp.open("POST", "http://localhost:8080/rest/occurrence/list", true);
            xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            var myJSON = JSON.stringify(user);
            xmlHttp.send(myJSON);

            xmlHttp.onreadystatechange = function () {
                if (xmlHttp.readyState == XMLHttpRequest.DONE) {
                    if(xmlHttp.status == 200){
                        var response = xmlHttp.response;
                        console.log("XML response: " + response);
                        obj = JSON.parse(response);

                        console.log(obj);

                        for(var occurrence in obj){
                            var map = obj[occurrence];

                            console.log(map);

                            var data = map.user_occurrence_data;
                            var level = map.user_occurrence_level;
                            var lat = map.user_occurrence_lat;
                            var long = map.user_occurrence_lon;
                            var title = map.user_occurrence_title;
                            var type  = map.user_occurrence_type;
                            var visibility = map.user_occurrence_visibility;
                        }
                            Operations.changeObj(obj);
                    }
                    else {
                        //TO DO- ver se o tempo ja expirou antes de "chatear" o server
                        console.log("tempo expirado");
                        //window.localStorage.removeItem('token');
                    }
                }
            }

        }
        this.setState({ occurrences: obj });
        console.log(this.state.occurrences);
    }

    changeObj = obj => {
        this.setState({ occurrences: obj });
        console.log(this.state.occurrences);
    }

    render() {
        const { classes } = this.props;

        return (
            <div onLoad={this.loadOperations}>
                <Typography variant="display1" className={classes.title}>Operacoes</Typography>

                <Table className={classes.table}>
                    <TableHead>
                        <TableRow>
                            <TableCell>Informações</TableCell>
                            <TableCell>Nome</TableCell>
                            <TableCell>Tipo</TableCell>
                            <TableCell>Data</TableCell>
                            <TableCell>Estado</TableCell>
                            <TableCell numeric>Grau de urgência</TableCell>
                            <TableCell>Visibilidade</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {operationsData.map(n => {
                            return (
                                <TableRow key={n.name}>
                                    <TableCell> <IconButton component={Link}
                                                            to="/operacao"> <InfoIcon/> </IconButton></TableCell>
                                    <TableCell>{n.name}</TableCell>
                                    <TableCell>{n.type}</TableCell>
                                    <TableCell>{n.date}</TableCell>
                                    <TableCell>{n.state}</TableCell>
                                    <TableCell numeric> {n.level}</TableCell>
                                    <TableCell>{n.visibility}</TableCell>
                                </TableRow>
                            );
                        })}
                    </TableBody>
                </Table>

                <Button onClick={this.loadOperations}>Pedido Rest</Button>
            </div>
        );
    }
}

Operations.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles) (Operations);