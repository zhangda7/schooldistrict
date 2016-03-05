function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]); return null;
}


var SchoolList = React.createClass({
  getInitialState: function() {
    return {
      schools : []
    };
  },

  componentDidMount: function() {
    /*$.ajax({
      url: this.props.source,
      success: function(data) {
        console.log(data);

        this.setState({schools: data});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(err.toString());
      }.bind(this)
    });*/

    $.get(this.props.source, function(result) {
        console.log(result);
        this.setState({schools : result});
    }.bind(this));
  },

  render: function() {
    //console.log(this.state.schools);
    return (
        <table className="table table-bordered table-striped">
          <thead>
          <tr>
            <th>
              小区名称
            </th>
            <th>
              地址
            </th>
          </tr>
          </thead>
          <tbody>
          {
            this.state.schools.map(function (row,i) {   //这里因为data是个数组，所以可以用map来遍历
              return (
                  <tr key={i}>
                    <td><a href="#" >{row.name}</a></td>  //取特定的值
                    <td>{row.address}</td>
                  </tr>
              );
            })
          }
          </tbody></table>
    );
  }
});

$(document).ready(function() {
    var estateID = getQueryString("id");
    ReactDOM.render(
        <SchoolList source={"/estate/estatelist.json?id=" + estateID} />,
        document.getElementById("example")
    );
});

