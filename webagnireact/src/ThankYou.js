import React from 'react';
import Typography from 'material-ui/Typography';
import {withStyles} from "material-ui/styles/index";

const styles =  theme => ({
    title: {
        margin: 20,
    },
});

class ThankYou extends React.Component {

    render() {
        const {classes} = this.props;

        return (
            <Typography variant="display3" gutterBottom>
                Obrigada por fazeres da terra um sitio melhor
            </Typography>
        )
    }
}

export default withStyles(styles)(ThankYou);