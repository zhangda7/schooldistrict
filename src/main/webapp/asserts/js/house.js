function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]); return null;
}


var SchoolList = React.createClass({
    getInitialState: function() {
        return {
            houses : []
        };
    },

    componentDidMount: function() {
        $.get(this.props.source, function(result) {
            console.log(result);
            this.setState({houses : result});
        }.bind(this));
    },

    render: function() {
        //console.log(this.state.schools);
        return (
            <div>
                <p>共{this.state.houses.length}套房子在售</p>
            <table className="table table-bordered table-striped">
                <thead>
                <tr>
                    <th>
                        标题
                    </th>
                    <th>
                        面积
                    </th>
                    <th>
                        价格
                    </th>
                    <th>
                        户型
                    </th>
                    <th>
                        最后更新时间
                    </th>
                </tr>
                </thead>
                <tbody>
                {
                    this.state.houses.map(function (row,i) {   //这里因为data是个数组，所以可以用map来遍历
                        return (
                            <tr key={i}>
                                <td><a href={row.url} >{row.title}</a></td>  //取特定的值
                                <td>{row.area}</td>
                                <td>{row.price}</td>
                                <td>{row.type}</td>
                                <td>{row.gmt_modified}</td>
                            </tr>
                        );
                    })
                }
                </tbody></table></div>
        );
    }
});

$(document).ready(function() {
    var houseID = getQueryString("id");
    ReactDOM.render(
        <SchoolList source={"/house/list.json?id=" + houseID} />,
        document.getElementById("example")
    );
});

