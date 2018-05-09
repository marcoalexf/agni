import React from 'react';
import Paper from 'material-ui/Paper';
import PropTypes from 'prop-types';
import FavoriteBorderIcon from '@material-ui/icons/FavoriteBorder';
import FavoriteIcon from '@material-ui/icons/Favorite';
import CommentBorderIcon from '@material-ui/icons/ChatBubbleOutline';
import CommentIcon from '@material-ui/icons/ChatBubble';
import Typography from 'material-ui/Typography';
import Checkbox from 'material-ui/Checkbox';
import { FormGroup, FormControlLabel } from 'material-ui/Form';
import {withStyles} from "material-ui/styles/index";

const styles =  theme => ({
    media: {
        margin: 40,
    },
    paper:theme.mixins.gutters({
        width: 800,
        padding: 40,
    }),
    opName:{
        fontFamily: 'Montserrat',
        fontSize: 48,
    },
    basicInfo:{
        display: 'inline-block',
        marginRight: 20,
        marginTop: 20,
        // marginBottom: 20,
        fontSize: 15,
    },
});

class Operation extends React.Component {
    state = {
        accountOpen: false,
        value: 0,
    };

    loadInformations = () =>{

    }

    render() {
        const {classes} = this.props;

        return(
            <div onLoad={this.loadInformations}>
                <Paper className={classes.paper} style={{margin: '0 auto'}} >

                    <div className={classes.informations}>
                        <div id="showopname" className={classes.opName}>Nome da operacao</div>
                        <Typography id="showoptype" component="p">Tipo de operacao</Typography>

                        <img src={require('./img/news2.jpg')} alt="Avatar" className={classes.media} width="600"/><br/>

                        <div className={classes.basicInfo}>
                            <FormControlLabel
                                control={
                                    <Checkbox icon={<FavoriteBorderIcon />} checkedIcon={<FavoriteIcon />} />
                                }
                                label="Apoiar"
                            />
                        </div>
                        <div className={classes.basicInfo}>
                            <FormControlLabel
                                control={
                                    <Checkbox icon={<CommentBorderIcon />} checkedIcon={<CommentIcon />} color={"primary"} />
                                }
                                label="Comentar"
                            />
                        </div>

                    </div>

                </Paper>
            </div>
        )

    }

}

Operation.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(Operation);