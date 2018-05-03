import React from 'react';
import TextField from 'material-ui/TextField';
import IconButton from 'material-ui/IconButton';
import Icon from 'material-ui/Icon';
import Visibility from '@material-ui/icons/Visibility';
import VisibilityOff from '@material-ui/icons/VisibilityOff';
import Typography from 'material-ui/Typography';
import Select from 'material-ui/Select';
import { InputLabel } from 'material-ui/Input';
import { MenuItem } from 'material-ui/Menu';
import Button from 'material-ui/Button';
import AddIcon from '@material-ui/icons/Add';
import Paper from 'material-ui/Paper';
import { FormControl, FormHelperText } from 'material-ui/Form';
import {withStyles} from "material-ui/styles/index";

const styles =  theme => ({
    title:{
        margin:20,
    },
    paper:theme.mixins.gutters({
        padding: 40,
    }),
    textField: {
        margin: theme.spacing.unit,
        width: 400,
    },
    formControl: {
        margin: theme.spacing.unit,
        width: 220,
    },
    selectEmpty: {
        marginTop: theme.spacing.unit * 2,
    },
});

class RegistProblem extends React.Component {

    state = {
        name: 'Nome do Registo do Problema',
        description: '',
        private: false,
        visibility: "public",
        problem: "limpeza",
        urgency: "3",
    };

    handleChange = name => event => {
        this.setState({
            [name]: event.target.value,
        });
    };

    handleMouseDownVisibility = event => {
        event.preventDefault();
    };

    handleVisibility = event => {
        if(event.target.value == 'private'){
            this.setState(
                {private: true}
                //{private: !this.state.private}
            );
        }
        else{
            this.setState({private: false});
        }

        this.setState({ [event.target.name]: event.target.value });
    }

    handleTypeChange = event => {
        this.setState({ [event.target.name]: event.target.value });
    };

    render(){
        const { classes } = this.props;
        return(
            <div>
                <Typography variant="display1" className={classes.title}>Registar Problema</Typography>

                <Paper className={classes.paper}>

                    <TextField
                        id="name"
                        label="Nome do registo"
                        onChange={this.handleChange('name')}
                        className={classes.textField}
                    />

                    <Button variant="raised" component="span">
                        Upload de imagem
                    </Button><br/>

                    <TextField
                        id="description"
                        label="Descricao"
                        onChange={this.handleChange('description')}
                        className={classes.textField}
                    />

                    <form autoComplete="off">
                        <FormControl className={classes.formControl}>
                            <InputLabel htmlFor="problem-name">Tipo de Problema</InputLabel>
                            <Select
                                value={this.state.problem}
                                onChange = {this.handleTypeChange}
                                inputProps={{
                                    name: 'problem',
                                    id: 'problem-name',
                                }}
                            >
                                <MenuItem value={1}>Limpeza de Mato</MenuItem>
                                <MenuItem value={2}>Zona de mau acesso</MenuItem>
                                <MenuItem value={3}>Outro</MenuItem>
                            </Select>
                        </FormControl>

                        <FormControl className={classes.formControl}>
                            <InputLabel htmlFor="urgency-level">Grau de urgencia:</InputLabel>
                            <Select
                                value={this.state.urgency}
                                onChange = {this.handleTypeChange}
                                inputProps={{
                                    name: 'urgency',
                                    id: 'urgency-level',
                                }}
                            >
                                <MenuItem value={1}>1</MenuItem>
                                <MenuItem value={2}>2</MenuItem>
                                <MenuItem value={3}>3</MenuItem>
                                <MenuItem value={4}>4</MenuItem>
                                <MenuItem value={5}>5</MenuItem>
                            </Select>
                        </FormControl>

                        <div id="visibility">
                            <FormControl className={classes.formControl}>
                                <Select
                                    value={this.state.visibility}
                                    onChange = {this.handleVisibility}
                                    displayEmpty
                                    name='visibility'
                                    className={classes.selectEmpty}
                                >
                                    <MenuItem value="public">Publico</MenuItem>
                                    <MenuItem value="private">Privado</MenuItem>
                                </Select>
                                <FormHelperText>Visibilidade</FormHelperText>
                            </FormControl>
                            <FormControl>
                                <Icon
                                    aria-label="regist visibility"
                                    color="default"
                                >
                                    {this.state.private ? <VisibilityOff /> : <Visibility />}
                                </Icon>
                            </FormControl>
                        </div>
                    </form>

                    <div id="submeter">
                        <Button variant="raised" size="small" color="primary" style={{margin:'0 auto'}}>
                            <AddIcon />
                            Registar Problema
                        </Button>
                    </div>
                </Paper>
            </div>
        );
    }
}

export default withStyles(styles)(RegistProblem);