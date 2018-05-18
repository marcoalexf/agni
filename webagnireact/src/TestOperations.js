import React from 'react';
import PropTypes from 'prop-types';
import Table, { TableBody, TableCell, TableHead, TableRow } from 'material-ui/Table';
import Async from 'react-promise';
import operationsData from './operationsData';
import Radio, { RadioGroup } from 'material-ui/Radio';
import InfoIcon from '@material-ui/icons/EventNote';
import IconButton from 'material-ui/IconButton';
import {withStyles} from "material-ui/styles/index";
import {Link} from "react-router-dom";

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

function makeAJAXCall(){
    //return new Promise(function(resolve, reject) {
            console.log("comeco da funcao");
            var map;
            var token = window.localStorage.getItem('token');
            var uname = JSON.parse(token).username;
            var tokenObj = JSON.parse(token);

            var user = {
                "username": uname,
                "token": tokenObj,
                "showPrivate": true //MUDAR ISTO DEPOIS
            }

            console.log("pedido");
            var xmlHttp = new XMLHttpRequest();
            xmlHttp.open("POST", "http://localhost:8080/rest/occurrence/list", true);
            xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            var myJSON = JSON.stringify(user);
            xmlHttp.send(myJSON);

            console.log("esperar pelo estado");
            xmlHttp.onreadystatechange = function () {
                console.log("1");
                if (xmlHttp.readyState === 4) {
                    console.log("2");
                    if (xmlHttp.status === 200) {
                        console.log("3");
                        var response = xmlHttp.response;
                        var obj = JSON.parse(response);
                        map = obj[0];
                        console.log(map);
                        // resolve(map);

                    }
                    else {
                        console.log("tempo expirado");
                        // reject(Error('Tempo expirado'));
                    }
                }
                else {
                    console.log("4");
                }
                //  }.bind(this);
            }
            // .bind(this);
        //}.bind(this)
    // );

}

let prom = new Promise(function(resolve, reject) {
    setTimeout(function() {
        resolve('a value')
    }, 100)
});

let xmlRequest = new Promise(function(resolve, reject) {
    console.log("xmlRequest");
    var t = true;
    var token = window.localStorage.getItem('token');
    var uname = JSON.parse(token).username;
    var tokenObj = JSON.parse(token);
    var map;

    var user = {
        "username": uname,
        "token": tokenObj,
        "showPrivate": true //MUDAR ISTO DEPOIS
    }

    console.log("pedido");
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open("POST", "http://localhost:8080/rest/occurrence/list", true);
    xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    var myJSON = JSON.stringify(user);
    xmlHttp.send(myJSON);

    console.log("esperar pelo estado");
    xmlHttp.onreadystatechange = function () {
        console.log("1");
        if (xmlHttp.readyState === 4) {
            console.log("2");
            if (xmlHttp.status === 200) {
                console.log("3");
                var response = xmlHttp.response;
                var obj = JSON.parse(response);
                console.log("obj:");
                console.log(obj);
                map = obj[0];
                console.log("map:");
                console.log(map);
                // var array = Object.values(map);
                // console.log(array);
                // console.log(operationsData);
                resolve(obj);
                // resolve('xml value')
            }
            else {
                console.log("tempo expirado");
                // reject(Error('Tempo expirado'));
                //TODO - Link to Login
            }
        }
    }

    // if(t=true)
    //     resolve('xml value')
})

const obj = [
    // xmlRequest.then((value) => {
    //     console.log("value:");
    //     console.log(value);
    //     return value;
    //     }
    // )
    // {user_occurrence_title: 'titulo1'},
    // {user_occurrence_title: 'titulo2'}
];


class TestOperations extends React.Component {
    state={
        object: [
            {user_occurrence_title: 'titulo1'},
            {user_occurrence_title: 'titulo2'}]
    };

    componentDidMount () {
        xmlRequest.then((value) =>{
                this.setState({object: value});
                console.log("state object");
                console.log(this.state.object);
            }
        );
    }

    render () {
        return (
            <div>
                <Table>
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
                        {this.state.object.map(n => {
                            return (
                                <TableRow key={n.user_occurrence_title}>
                                    <TableCell> <IconButton component={Link}
                                                            to="/operacao"> <InfoIcon/> </IconButton></TableCell>
                                    <TableCell>{n.user_occurrence_title}</TableCell>
                                    <TableCell>{n.user_occurrence_type}</TableCell>
                                    <TableCell>{n.user_occurrence_data}</TableCell>
                                    <TableCell>{'nao tratado'}</TableCell>
                                    <TableCell numeric> {n.user_occurrence_level}</TableCell>
                                    <TableCell>{n.user_occurrence_visibility ? 'publico' : 'privado'}</TableCell>
                                </TableRow>
                            );
                        })}
                    </TableBody>
                </Table>
            </div>
        )
    }
}

TestOperations.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles) (TestOperations);