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

function OperationsTable(props){
    var map = {user_occurrence_title: 'ana'};
    var token = window.localStorage.getItem('token');
    var uname = JSON.parse(token).username;
    var tokenObj = JSON.parse(token);

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

    xmlHttp.onload = function (e) {
        if (xmlHttp.readyState === 4) {
            if(xmlHttp.status === 200){
                var response = xmlHttp.response;
                var obj = JSON.parse(response);
                map = obj[0];
                this.setState({ occurrences: map });
                console.log("state.occurrences:")
                console.log(this.state.occurrences);
                console.log(obj);
            }
            else {
                //TO DO- ver se o tempo ja expirou antes de "chatear" o server
                console.log("tempo expirado");
                //window.localStorage.removeItem('token');
            }
        }
    }.bind(this);

    const table = map.map((occ) =>
        <TableBody>
            <TableRow key={occ.user_occurrence_title}>
                <TableCell> <IconButton component={Link}
                                        to="/operacao"> <InfoIcon/> </IconButton></TableCell>
                <TableCell>{occ.user_occurrence_title}</TableCell>
                <TableCell>{occ.user_occurrence_type}</TableCell>
                <TableCell>{occ.user_occurrence_data}</TableCell>
                <TableCell>{'nao tratado'}</TableCell>
                <TableCell numeric> {occ.user_occurrence_level}</TableCell>
                <TableCell>{occ.user_occurrence_visibility}</TableCell>
            </TableRow>
        </TableBody>
    );

    return(
        {table}
    );
}

class Operations extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            occurrences: 'null',
        };

        this.loadOperations = this.loadOperations.bind(this);
    }

    loadOperations = () => {
        console.log("aqui");
        var obj;
        var token = window.localStorage.getItem('token');

        //if(token != null) {
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

            xmlHttp.onload = function (e) {
                if (xmlHttp.readyState === 4) {
                    if(xmlHttp.status === 200){
                        var response = xmlHttp.response;
                        //console.log("XML response: " + response);
                        obj = JSON.parse(response);
                        var map = obj[0];
                        this.setState({ occurrences: map });
                        console.log("state.occurrences:")
                        console.log(this.state.occurrences);
                        console.log(obj);

                        return(map.map((occ) =>
                            <TableBody>
                                <TableRow key={occ.user_occurrence_title}>
                                    <TableCell> <IconButton component={Link}
                                                            to="/operacao"> <InfoIcon/> </IconButton></TableCell>
                                    <TableCell>{occ.user_occurrence_title}</TableCell>
                                    <TableCell>{occ.user_occurrence_type}</TableCell>
                                    <TableCell>{occ.user_occurrence_data}</TableCell>
                                    <TableCell>{'nao tratado'}</TableCell>
                                    <TableCell numeric> {occ.user_occurrence_level}</TableCell>
                                    <TableCell>{occ.user_occurrence_visibility}</TableCell>
                                </TableRow>
                            </TableBody>
                            )
                        );
                    }
                    else {
                        //TO DO- ver se o tempo ja expirou antes de "chatear" o server
                        console.log("tempo expirado");
                        //window.localStorage.removeItem('token');
                    }
                }
            //}

        }.bind(this);
    }



    render() {
        const { classes } = this.props;

        return (
            <div>
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
                        {operationsData.map(n =>
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
                        )}
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