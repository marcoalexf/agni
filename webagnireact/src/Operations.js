import React, { Component } from 'react';
import Table, { TableBody, TableCell, TableHead, TableRow } from 'material-ui/Table';
import operationsData from './operationsData';
import InfoIcon from '@material-ui/icons/Info';
import IconButton from 'material-ui/IconButton';
import PropTypes from 'prop-types';
import {withStyles} from "material-ui/styles/index";

const styles = theme => ({
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
    render() {
        const { classes } = this.props;

        return (
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
                                <TableCell> <IconButton> <InfoIcon/> </IconButton></TableCell>
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
        );
    }
}

Operations.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles) (Operations);