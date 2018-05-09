import React from 'react';
import TextField from 'material-ui/TextField';
import SearchIcon from '@material-ui/icons/Search';
import Icon from 'material-ui/Icon';
import Visibility from '@material-ui/icons/Visibility';
import VisibilityOff from '@material-ui/icons/VisibilityOff';
import Typography from 'material-ui/Typography';
import Select from 'material-ui/Select';
import { InputLabel } from 'material-ui/Input';
import { MenuItem } from 'material-ui/Menu';
import Button from 'material-ui/Button';
import AddIcon from '@material-ui/icons/Add';
import WallpaperIcon from '@material-ui/icons/Wallpaper';
import Paper from 'material-ui/Paper';
import Checkbox from 'material-ui/Checkbox';
import NotificationsOff from '@material-ui/icons/NotificationsOff';
import NotificationsActive from '@material-ui/icons/NotificationsActive';
import { FormControl, FormHelperText } from 'material-ui/Form';
import { FormGroup, FormControlLabel } from 'material-ui/Form';
import {withStyles} from "material-ui/styles/index";
import GoogleMapReact from 'google-map-react';

const AnyReactComponent = ({ text }) => <div>{text}</div>;

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
    uploadButton: {
        marginLeft: 500,
    },
    map:{
        display: 'inline-block',
    },
    properties:{
        display: 'inline-block',
    },
    regButton:{
        marginLeft: 1000,
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
        location: '',
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

    static defaultProps = {
        center: {
            lat: 59.95,
            lng: 30.33
        },
        zoom: 11
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

                    <Button variant="raised" component="span" className={classes.uploadButton}>
                        Upload de imagem <WallpaperIcon size="large"/>
                    </Button>


                    <TextField
                        id="description"
                        label="Descricao"
                        multiline
                        rows="4"
                        onChange={this.handleChange('description')}
                        className={classes.textField}
                    /><br/>

                    <div>
                        <TextField
                            id="location"
                            label="Localizacao"
                            onChange={this.handleChange('location')}
                            className={classes.textField}
                        />
                        <Button variant="raised" size="small" color="primary"> <SearchIcon/> </Button>
                    </div>

                    <div style={{ height: '40vh', width: '40%' }}>
                        <GoogleMapReact
                            bootstrapURLKeys={{ key: 'AIzaSyAtTD5VCFlMDX0HcnPbnZWWArACgGR5Ywk' }}
                            defaultCenter={this.props.center}
                            defaultZoom={this.props.zoom}
                        >
                            <AnyReactComponent
                                lat={59.955413}
                                lng={30.337844}
                                text={'Kreyser Avrora'}
                            />
                        </GoogleMapReact>
                    </div>

                    <div className={classes.properties}>
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
                    </div>

                    <div>
                        <FormControlLabel
                            control={
                                <Checkbox icon={<NotificationsOff />} checkedIcon={<NotificationsActive />} color={"primary"} />
                            }
                            label="Notificar quando estiver resolvido"
                        />
                    </div>

                    <div id="submeter" className={classes.regButton}>
                        <Button variant="raised" size="small" color="primary"  style={{margin:'0 auto'}}>
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