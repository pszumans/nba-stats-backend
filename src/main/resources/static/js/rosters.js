class Rosters extends React.Component {
    constructor() {
        super();
        this.state = {
            teams: []
        };
    }

    componentDidMount() {

        fetch("/api/rosters")
            .then(response => response.json())
            .then(data => {
                this.setState({teams: data})
            })
    }

    render() {

        if (this.state.teams.length === 0) {
            return (<p>Loading...</p>);
        } else {
            let teams = this.state.teams.map(team => <Team team={team}/>);
        }
        return (<ReactBootstrap.Table striped>{teams}</ReactBootstrap.Table>);

    }
}

class Team extends React.Component {
    static propTypes = {
        team: PropTypes.object
    };

    constructor() {
        super();
        this.state = {
            show: false
        };
    }

    handleClick = () => {
        this.setState({
            show: !this.state.show
        });
    };

    render() {
        const team = this.props.team;
        const players = team.players.map(player => <Player player={player}/>);

        const {show} = this.state;
        const label = show ? "Hide" : "Show"
        return (
            <div>
                <thead>
                <tr key={team.teamId}>
                    <th>{team.fullName}</th>
                    <button onClick={this.handleClick}>{label}</button>
                </tr>
                </thead>
                {show && (<tbody>{players}</tbody>)}
            </div>
        );
    }
}

class Player extends React.Component {
    static propTypes = {
        player: PropTypes.shapeOf({
            personId: PropTypes.number.isRequired,
        }).isRequired
    };

    render() {
        const player = this.props.player;
        return (<tr key={player.personId}>
            <td>{player.pos}</td>
            <td>{player.jersey}</td>
            <td>{player.firstName}</td>
            <td>{player.lastName}</td>
        </tr>);
    }
}

ReactDOM.render(
    <Rosters/>,
    document.getElementById('root')
);
