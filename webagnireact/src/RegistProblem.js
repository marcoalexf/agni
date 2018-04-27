import React from 'react';
import TextField from 'material-ui/TextField';
import IconButton from 'material-ui/IconButton';
import Visibility from '@material-ui/icons/Visibility';
import VisibilityOff from '@material-ui/icons/VisibilityOff';
import Typography from 'material-ui/Typography';
import Select from 'material-ui/Select';
import { InputLabel } from 'material-ui/Input';
import { MenuItem } from 'material-ui/Menu';
import Button from 'material-ui/Button';
import AddIcon from '@material-ui/icons/Add';

class RegistProblem extends React.Component {

    state = {
        name: 'Nome do Registo do Problema',
        private: false,
        visibility: "publico",
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

    handleVisibility = () => {
        this.setState({private: !this.state.private});
    }

    handleTypeChange = event => {
        this.setState({ [event.target.name]: event.target.value });
    };

    render(){
        return(
            <div>
                <div id="registername">
                    <TextField
                        id="name"
                        label="Nome do registo"
                        onChange={this.handleChange('name')}
                        margin="normal"
                    />
                </div>

                <div id="picture">
                    <Button variant="raised" component="span">
                        Upload de imagem
                    </Button>
                </div>

                <div id="type of problem">
                    <InputLabel htmlFor="age-simple">Tipo de problema:</InputLabel>
                    <Select
                        value={this.state.problem}
                        onChange={this.handleTypeChange}
                        inputProps={{
                            name: 'age',
                            id: 'age-simple',
                        }}
                    >

                        <MenuItem value={1}>Limpeza de Mato</MenuItem>
                        <MenuItem value={2}>Zona de mau acesso</MenuItem>
                        <MenuItem value={3}>Outro</MenuItem>
                    </Select>
                </div>

                <div>
                    <InputLabel htmlFor="age-simple">Grau de urgencia:</InputLabel>
                    <Select
                        value={this.state.urgency}
                        inputProps={{
                            name: 'age',
                            id: 'age-simple',
                        }}
                    >

                        <MenuItem value={1}>1</MenuItem>
                        <MenuItem value={2}>2</MenuItem>
                        <MenuItem value={3}>3</MenuItem>
                        <MenuItem value={4}>4</MenuItem>
                        <MenuItem value={5}>5</MenuItem>
                    </Select>
                </div>

                <div>
                    <TextField
                        id="dataderegisto"
                        label="Data do registo"
                        margin="normal"
                    />
                </div>

                <div id="visibility">
                    <Typography component="title">
                        Visibilidade
                        <IconButton
                            aria-label="Toggle password visibility"
                            onClick={this.handleVisibility}
                            onMouseDown={this.handleMouseDownVisibility}
                        >
                            {this.state.private ? <VisibilityOff /> : <Visibility />}
                        </IconButton>
                    </Typography>
                </div>

                <div id="submeter">
                    <Button variant="raised" size="small" color="secondary">
                        <AddIcon />
                        Registar Problema
                    </Button>
                </div>

            </div>
        );
    }
}

export default RegistProblem;