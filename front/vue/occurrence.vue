<template>
    <div class="container">
        <ol class="breadcrumb">
            <li><a href="#/">Inicio</a></li>
            <li><a href="#/panel-tweets">Panel de tweets</a></li>
            <li class="active">Detalle</li>
        </ol>
        <div class="row occurrence">
            <div class="col-md-5 column">
                <tweet v-bind="occurrence"></tweet>
                <div id="map" class="area"></div>
            </div>
            <div class="col-md-7 column">
                <div class="panel area" style="margin-bottom: 0" id="graph">
                    <div class="info node-info" id="info">
                        <b style="text-align: center">SIMBOLOGÍA</b>
                        <br><br>
                        <ul>
                            <li style="color: #DD1C1A">Evento</li>
                            <li style="color: #29BF12">Comuna</li>
                            <li style="color: #FF9914">Usuario</li>
                        </ul>
                        Posicione el mouse sobre un nodo para ver mayor información...
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import Tweet from './tweet.vue';

export default {
    props: ["id"],
    data: function () {
        return {
            occurrence: {}
        }
    },
    components: {
        'tweet': Tweet
    },
    mounted: function() {
        this.$http.get("http://traffictweet.ddns.net:9090/traffictweet/occurrences/" + this.id)
            .then(response=>{
                this.occurrence = response.body;
                this.putMap();
            }, response=>{
                console.log('Error cargando evento');
            });

        this.$http.get("http://traffictweet.ddns.net:9090/traffictweet/nodes/occurrences/" + this.id)
            .then(occurrence=>{
                this.$http.get("http://traffictweet.ddns.net:9090/traffictweet/nodes/occurrences/" + this.id + "/causes")
                    .then(causes=>{
                        const graph = this.getGraph(occurrence.body, causes.body);
                        this.putGraph(graph);
                    });
            });
    },
    methods: {
        putMap: function() {
            this.map = L.map('map').setView([this.occurrence.commune.x, this.occurrence.commune.y], this.occurrence.commune.z);
            L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
                maxZoom: 18,
                id: 'mapbox.streets-basic',
                accessToken: 'pk.eyJ1IjoicmhhZGFteXMiLCJhIjoiY2o4bzBoZGVxMWU3bzJ3cGZtdTJucXkyMiJ9.pkzq5crdrE9U5HpXdl6Ing'
            }).addTo(this.map);
        },
        putGraph: function(graph) {
            const graphElm = document.getElementById('graph');
            let width = graphElm.clientWidth, height = graphElm.clientHeight;
            // force layout setup
            let force = d3.layout.force()
                .charge(-1000).linkDistance(200).size([width, height]);

            // setup svg div
            let svg = d3.select("#graph").append("svg:svg")
                .attr("width", "100%").attr("height", "100%")
                .attr("pointer-events", "all");

            // load graph (nodes,links) json from /graph endpoint
            d3.json("", function(error) {
                if (error) return;

                force.nodes(graph.nodes)
                    .links(graph.links)
                    .start();

                // render relationships as lines
                let link = svg.selectAll(".link")
                    .data(graph.links).enter()
                    .append("line").attr("class", "link");

                // render nodes as circles, css-class from label
                let node = svg.selectAll(".node")
                    .data(graph.nodes).enter()
                    .append("circle")
                    .attr("r", 40)
                    .attr("class", function(d) { return "node " + d.label; })
                    .on("mouseover", function(d) {
                        switch(d.label) {
                            case 'current occurrence':
                            case 'occurrence': {
                                info.innerHTML = '<b>Fecha:</b><br>' + d.date + '<br><br>' +
                                    '<b>Reportado en:</b><br>' + d.commune.name  + '<br><br>' +
                                    '<b>Reportado por:</b><br>' + d.reportedBy.username;
                                break;
                            }
                            case 'commune': {
                                info.innerHTML = '<b>Comuna:</b><br>' + d.name;
                                break;
                            }
                            case 'user': {
                                info.innerHTML = '<b>Nombre de usuario:</b><br>' + d.username;
                                break;
                            }
                        }
                    })
                    .on('mouseout', function(d){
                        info.innerHTML = '<b style="text-align: center">SIMBOLOGÍA</b><br><br><ul>' +
                            '<li style="color: #DD1C1A">Evento</li>' +
                            '<li style="color: #29BF12">Comuna</li>' +
                            '<li style="color: #FF9914">Usuario</li>' +
                            '</ul>Posicione el mouse sobre un nodo para ver mayor información...';
                    })
                    .call(force.drag);

                let edgepaths = svg.selectAll(".edgepath")
                    .data(graph.links)
                    .enter()
                    .append('path')
                    .attr({'d': function(d) { return 'M ' + d.source.x + ' ' + d.source.y + ' L ' +
                        d.target.x + ' ' + d.target.y },
                        'class': 'edgepath',
                        'fill-opacity': 0,
                        'stroke-opacity': 0,
                        'fill': 'blue',
                        'stroke': 'red',
                        'id':function(d,i) {return 'edgepath'+i}})
                    .style("pointer-events", "none");

                let edgelabels = svg.selectAll(".edgelabel")
                    .data(graph.links)
                    .enter()
                    .append('text')
                    .style("pointer-events", "none")
                    .attr({'class': 'edgelabel',
                        'id': function(d,i){return 'edgelabel'+i},
                        'dx': 60,
                        'dy': -5,
                        'font-size': 12,
                        'font-weight' : 'bold',
                        'fill': '#005A32'
                    });

                edgelabels.append('textPath')
                    .attr('xlink:href',function(d,i) {return '#edgepath'+i})
                    .style("pointer-events", "none")
                    .text(function(d){ return '[' + d.type + ']' });

                svg.append('defs').append('marker')
                    .attr({'id':'arrowhead',
                        'viewBox':'-0 -5 10 10',
                        'refX':25,
                        'refY':0,
                        'orient':'auto',
                        'markerWidth':10,
                        'markerHeight':10,
                        'xoverflow':'visible'})
                    .append('svg:path')
                    .attr('d', 'M 0,-5 L 10 ,0 L 0,5')
                    .attr('fill', '#ccc')
                    .attr('stroke','#ccc');

                // force feed algo ticks for coordinate computation
                force.on("tick", function() {
                    link.attr("x1", function(d) { return d.source.x; })
                        .attr("y1", function(d) { return d.source.y; })
                        .attr("x2", function(d) { return d.target.x; })
                        .attr("y2", function(d) { return d.target.y; });

                    node.attr("cx", function(d) { return d.x; })
                        .attr("cy", function(d) { return d.y; });

                    edgepaths.attr('d', function(d) { return 'M ' + d.source.x + ' ' + d.source.y + ' L ' +
                        d.target.x + ' ' + d.target.y; });

                    edgelabels.attr('transform', function(d){
                        if (d.target.x < d.source.x){
                            const bbox = this.getBBox();
                            const rx = bbox.x + bbox.width / 2;
                            const ry = bbox.y + bbox.height / 2;
                            return 'rotate(180 ' + rx + ' ' + ry + ')';
                        }
                        else {
                            return 'rotate(0)';
                        }
                    });
                });
            });
        },
        getGraph: function(occurrence, causes) {
            occurrence.label = 'current occurrence';
            occurrence.commune.label = 'commune';
            occurrence.reportedBy.label = 'user';

            let nodes = [
                occurrence,
                occurrence.commune,
                occurrence.reportedBy];
            let links = [{
                source: 0,
                target: 1,
                type: 'REPORTED_AT'
            },{
                source: 0,
                target: 2,
                type: 'REPORTED_BY'
            }];

            if(causes) {
                let communeNames = [occurrence.commune.name];
                let communeIndexes = [1];

                let userNames = [occurrence.reportedBy.username];
                let userIndexes = [2];

                causes.forEach((cause) => {
                    cause.label = 'occurrence';
                    nodes.push(cause);

                    links.push({
                        source: 0,
                        target: nodes.length - 1,
                        type: 'DUE_TO'
                    });

                    // Communes
                    let communeExist = true;
                    if(!communeNames.includes(cause.commune.name)) {
                        communeExist = false;
                        cause.commune.label = 'commune';
                        nodes.push(cause.commune);
                        communeNames.push(cause.commune.name);
                        communeIndexes.push(nodes.length - 1);
                    }

                    links.push({
                        source: nodes.length - (communeExist ? 1 : 2),
                        target: communeIndexes[communeNames.indexOf(cause.commune.name)],
                        type: 'REPORTED_AT'
                    });

                    // Users
                    let userExist = true;
                    if(!userNames.includes(cause.reportedBy.username)) {
                        userExist = false;
                        cause.reportedBy.label = 'user';
                        nodes.push(cause.reportedBy);
                        userNames.push(cause.reportedBy.username);
                        userIndexes.push(nodes.length - 1);
                    }

                    links.push({
                        source: nodes.length - (communeExist && userExist ? 1 :
                            communeExist || userExist ? 2 : 3),
                        target: userIndexes[userNames.indexOf(cause.reportedBy.username)],
                        type: 'REPORTED_BY'
                    });
                });
            }

            return {
                nodes: nodes,
                links: links
            }
        }
    }
}
</script>